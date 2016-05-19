package com.dianping.swallow.test.load.dao.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.bson.types.BSONTimestamp;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.test.load.dao.AbstractDaoTest;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年1月26日 下午9:55:10
 */
public class MongoInsertCollectionTest extends AbstractDaoTest {

    private static int concurrentCount = 100;

    private static int dbCount = 1;
    private static int collectionCount = 1;

    protected static boolean hasDate = Boolean.parseBoolean(System.getProperty("hasDate", "false"));
    protected static boolean hasTimeStamp = Boolean.parseBoolean(System.getProperty("hasTimeStamp", "true"));
    protected static boolean isMessageRandom = Boolean.parseBoolean(System.getProperty("isMessageRandom", "false"));

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        if (args.length >= 1) {
            dbCount = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            collectionCount = Integer.parseInt(args[1]);
        }


        new MongoInsertCollectionTest().start();
    }

    @Override
    protected void doStart() throws InterruptedException, IOException {

        sendMessage();
    }

    private void sendMessage() throws IOException {

        MongoClient mongo = getMongo();

        for (int i = 0; i < dbCount; i++) {

            DB db = mongo.getDB("msg#" + getTopicName(topicName, i));

            for (int j = 0; j < collectionCount; j++) {

                for (int k = 0; k < concurrentCount; k++) {

                    executors.execute(new TaskSaveMessage(db, j));
                }
            }
        }

    }


    class TaskSaveMessage implements Runnable {

        private DB db;

        private int index;

        public TaskSaveMessage(DB db, int index) {
            this.db = db;
            this.index = index;
        }

        @Override
        public void run() {

            while (true) {
                try {
                    DBCollection collection = db.getCollection("c" + index);
                    collection.save(createSimpleDataObject());
                    increaseAndGetCurrentCount();
                } catch (Throwable e) {
                    logger.error("[run]", e);
                }
            }
        }
    }

    private DBObject createSimpleDataObject() {

        DBObject object = new BasicDBObject();
        if (!isMessageRandom) {
            object.put("c", message);
        } else {
            object.put("c", createMessage());
        }
        //object.put("_id", new BSONTimestamp());
        if (hasTimeStamp) {
            object.put("t", new BSONTimestamp());
        }
        if (hasDate) {
            object.put("d", new Date());
        }
//		object.put("t1", new BSONTimestamp());
//		object.put("t2", new BSONTimestamp());
//		object.put("t3", new BSONTimestamp());
//		object.put("t4", new BSONTimestamp());
        return object;
    }


    /**
     * @return
     * @throws IOException
     */
    private String getTopicToMongo() throws IOException {

        Properties p = new Properties();
        InputStream ins = getClass().getClassLoader().getResourceAsStream("swallow-store-lion.properties");
        if (ins == null) {
            throw new IllegalStateException("file not found: swallow-store-lion.properties");
        }
        p.load(ins);
        String result = p.getProperty("swallow.topiccfg.default");
        if (StringUtils.isBlank(result)) {
            throw new IllegalStateException("swallow.mongo.producerServerURI not found!!!");
        }
        return result;
    }


    protected MongoClient getMongo() throws IOException {

        String topicToMongo = getTopicToMongo();
        List<ServerAddress> addresses = getAddress(topicToMongo);
        return new MongoClient(addresses, buildMongoOptions());
    }

    protected MongoClientOptions buildMongoOptions() {

        MongoClientOptions.Builder builder = MongoClientOptions.builder();

        builder.socketKeepAlive(true);
        builder.socketTimeout(5000);
        builder.connectionsPerHost(100);
        builder.threadsAllowedToBlockForConnectionMultiplier(5);
        builder.connectTimeout(2000);
        builder.maxWaitTime(2000);

        builder.writeConcern(new WriteConcern(1, 5000, false, false));
        builder.readPreference(ReadPreference.nearest());

        return builder.build();
    }

    /**
     * 获取默认地址
     *
     * @param topicToMongo
     * @return
     * @throws UnknownHostException
     * @throws NumberFormatException
     */
    private List<ServerAddress> getAddress(String topicToMongo) throws NumberFormatException, UnknownHostException {

        TopicConfig config = JsonBinder.getNonEmptyBinder().fromJson(topicToMongo, TopicConfig.class);

        String strAddresses = config.getStoreUrl().substring("mongodb://".length());
        String[] addressArr = strAddresses.split(",");
        List<ServerAddress> addresses = new ArrayList<ServerAddress>();

        if (addressArr != null && addressArr.length > 0) {
            for (String strAddress : addressArr) {
                String[] ipPort = strAddress.split(":");
                ServerAddress serverAddress = new ServerAddress(ipPort[0], Integer.parseInt(ipPort[1]));
                addresses.add(serverAddress);
            }

        }
        return addresses;

    }

}

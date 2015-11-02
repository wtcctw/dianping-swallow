package com.dianping.swallow.test.load.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigImpl;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoMessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.test.load.AbstractLoadTest;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * @author mengwenchao
 *
 * 2015年3月24日 下午6:26:06
 */
public abstract class AbstractMongoTest extends AbstractLoadTest{
	
	protected MessageDAO dao;
	

	@Override
	protected void start() throws Exception {
		
		dao = createDao();
		super.start();
		
	};
	
	protected MessageDAO createDao() throws Exception {
		
		System.setProperty("lion.useLocal", "true");
		DefaultMongoManager mc = new DefaultMongoManager();
		mc.setSwallowConfig(new SwallowConfigImpl());
		mc.initialize();
		
		MongoMessageDAO mdao = new MongoMessageDAO();
		mdao.setMongoManager(mc);
		return mdao;
	}

	/**
	 * @return
	 * @throws IOException 
	 */
	private String getTopicToMongo() throws IOException {
		
		Properties p = new Properties();
		InputStream ins = getClass().getClassLoader().getResourceAsStream("swallow-mongo-lion.properties");
		if(ins == null){
			throw new IllegalStateException("file not found: swallow-mongo-lion.properties");
		}
		p.load(ins);
		String result = p.getProperty("swallow.topiccfg.default");
		if(StringUtils.isBlank(result)){
			throw new IllegalStateException("swallow.mongo.producerServerURI not found!!!");
		}
		return result;
	}

	
	protected MongoClient getMongo() throws IOException{
		
		String topicToMongo = getTopicToMongo();
		ServerAddress address = getAddress(topicToMongo);
		return new MongoClient(address);
	}
	
	
	
	/**
	 * 获取默认地址
	 * @param topicToMongo
	 * @return
	 * @throws UnknownHostException 
	 * @throws NumberFormatException 
	 */
	private ServerAddress getAddress(String topicToMongo) throws NumberFormatException, UnknownHostException {

		TopicConfig config = JsonBinder.getNonEmptyBinder().fromJson(topicToMongo, TopicConfig.class);
		
		String address = config.getStoreUrl().substring("mongodb://".length());
		String []ipPort = address.split(":");
		return new ServerAddress(ipPort[0], Integer.parseInt(ipPort[1]));
				
	}

	protected SwallowMessage createMessage(String content) {
		
		SwallowMessage message = new SwallowMessage();
		message.setContent(content);
		message.setGeneratedTime(new Date());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("property-key", "property-value");
		message.setProperties(map);
		message.setSha1("sha-1 string");
		message.setVersion("0.6.0");
		message.setType("feed");
		message.setSourceIp("localhost");
		return message;
	}

	
	protected void sendMessage(int topicCount, int concurrentCount, final int messageCount) {

		logger.info("[doStart][config]" + topicCount + "," + concurrentCount + "," + messageCount);
		

		for(int i = 0 ;i< topicCount; i++){
			
			for(int j=0;j<concurrentCount;j++){
				
				final String realTopicName = getTopicName(topicName, i);
				executors.execute(new Runnable(){
					@Override
					public void run() {
	
						while(true){
							try{
								if(count.get() > messageCount){
									exit();
								}
								saveMessge(realTopicName);
							}catch(Exception e){
								logger.error("error save message", e);
							}finally{
							}
						}
						
					}
				});
			}
		}
	}

	protected void saveMessge(String topicName) {
		
		dao.saveMessage(topicName, createMessage(message));
		count.incrementAndGet();
		
	}

}

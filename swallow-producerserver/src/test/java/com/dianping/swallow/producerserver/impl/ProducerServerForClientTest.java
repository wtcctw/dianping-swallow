package com.dianping.swallow.producerserver.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Date;

import jmockmongo.MockMongo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.packet.PacketType;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.packet.PktProducerGreet;
import com.dianping.swallow.common.internal.packet.PktSwallowPACK;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.SHAUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.producer.exceptions.ServerDaoException;
import com.dianping.swallow.common.server.monitor.collector.DefaultProducerCollector;

public class ProducerServerForClientTest {

    private static final String UNIT_TEST = "UnitTest";
    static MockMongo            mockMongo;
    static MockMongo            mockMongo2;

    @Test
    public void testProducerServerForClient() throws Exception {
        //初始化ProducerServerForClient对象
        ProducerServerForClient producerServerForClient = new ProducerServerForClient();
        producerServerForClient.setProducerCollector(new DefaultProducerCollector());
        TopicWhiteList topicWhiteList = new TopicWhiteList();
        topicWhiteList.addTopic(UNIT_TEST);
        producerServerForClient.setTopicWhiteList(topicWhiteList);

        int port = 4000;
        producerServerForClient.setPort(port);
        producerServerForClient.setRemoteServiceName("remoteService");

        Assert.assertEquals(port, producerServerForClient.getPort());

        //启动Service服务
        producerServerForClient.start();

        //构造greet
        PktProducerGreet pktProducerGreet = new PktProducerGreet("0.6.0", "Unit Test");
        //构造message
        SwallowMessage swallowMessage = new SwallowMessage();
        swallowMessage.setContent(UNIT_TEST);
        swallowMessage.setGeneratedTime(new Date());
        swallowMessage.setSourceIp("192.168.32.194");
        swallowMessage.setVersion("0.6.0");
        PktMessage pktMessage = new PktMessage(Destination.topic(UNIT_TEST), swallowMessage);

        PktSwallowPACK ACK = null;
        //发送greet
        ACK = (PktSwallowPACK) producerServerForClient.sendMessage(pktProducerGreet);

        Assert.assertEquals(PacketType.SWALLOW_P_ACK, ACK.getPacketType());
        Assert.assertEquals(IPUtil.getFirstNoLoopbackIP4Address(), ACK.getShaInfo());

        //不抛异常的DAO
        MessageDAO messageDAO = mock(MessageDAO.class);
        producerServerForClient.setMessageDAO(messageDAO);
        ACK = (PktSwallowPACK) producerServerForClient.sendMessage(pktMessage);
        Assert.assertEquals(PacketType.SWALLOW_P_ACK, ACK.getPacketType());
        Assert.assertEquals(SHAUtil.generateSHA(swallowMessage.getContent()), ACK.getShaInfo());

        //设置mock行为，抛出数据库异常
        doThrow(new RuntimeException()).when(messageDAO).saveMessage(Matchers.anyString(), (SwallowMessage) Matchers.anyObject());
        ACK = null;
        try {
            ACK = (PktSwallowPACK) producerServerForClient.sendMessage(pktMessage);
        } catch (ServerDaoException e) {
        }
        Assert.assertNull(ACK);

        System.out.println(producerServerForClient.getRemoteServiceName());
    }

    //    @Test
    //    public void testProducerServerForClientWithRealDAO() throws UnknownHostException {
    //        //初始化ProducerServerForClient对象
    //        ProducerServerForClient producerServerForClient = new ProducerServerForClient();
    //
    //        //构造message
    //        SwallowMessage swallowMessage = new SwallowMessage();
    //        swallowMessage.setContent(UNIT_TEST);
    //        swallowMessage.setGeneratedTime(new Date());
    //        swallowMessage.setSourceIp("192.168.32.194");
    //        swallowMessage.setVersion("0.6.0");
    //        PktMessage pktMessage = new PktMessage(Destination.topic(UNIT_TEST), swallowMessage);
    //
    //        PktSwallowPACK ACK = null;
    //
    //        //mock的lion配置
    //        DynamicConfig config = mock(LionDynamicConfig.class);
    //        when(config.get("swallow.mongo.producerServerURI")).thenReturn("default=mongodb://127.0.0.1:27010;feed=mongodb://127.0.0.1:21011");
    //        when(config.get("swallow.mongo.msgCappedCollectionSize")).thenReturn("default=1024;feed,topicForUnitTest=1025");
    //        when(config.get("swallow.mongo.msgCappedCollectionMaxDocNum")).thenReturn("default=1024;feed,topicForUnitTest=1025");
    //        when(config.get("swallow.mongo.ackCappedCollectionSize")).thenReturn("default=1024;feed,topicForUnitTest=1025");
    //        when(config.get("swallow.mongo.ackCappedCollectionMaxDocNum")).thenReturn("default=1024;feed,topicForUnitTest=1025");
    //        when(config.get("swallow.mongo.heartbeatServerURI")).thenReturn("mongodb://localhost:24521");
    //        when(config.get("swallow.mongo.heartbeatCappedCollectionSize")).thenReturn("1025");
    //        when(config.get("swallow.mongo.heartbeatCappedCollectionMaxDocNum")).thenReturn("1025");
    //        //真实的mongoClient
    //        MongoClient mongoClient = new MongoClient("swallow.mongo.producerServerURI", config);
    //
    //        //不抛异常的DAO
    //        MessageDAOImpl messageDAOImpl = new MessageDAOImpl();
    //        messageDAOImpl.setMongoClient(mongoClient);
    //
    //        producerServerForClient.setMessageDAO(messageDAOImpl);
    //
    //        ACK = (PktSwallowPACK) producerServerForClient.sendMessage(pktMessage);
    //        Assert.assertEquals(PacketType.SWALLOW_P_ACK, ACK.getPacketType());
    //        Assert.assertEquals(SHAUtil.generateSHA(swallowMessage.getContent()), ACK.getShaInfo());
    //
    //        mongoClient.onConfigChange("swallow.mongo.producerServerURI", "default=mongodb://127.0.0.1:27010;feed=mongodb://127.0.0.1:21011");
    //
    //        ACK = (PktSwallowPACK) producerServerForClient.sendMessage(pktMessage);
    //        Assert.assertEquals(PacketType.SWALLOW_P_ACK, ACK.getPacketType());
    //        Assert.assertEquals(SHAUtil.generateSHA(swallowMessage.getContent()), ACK.getShaInfo());
    //
    //    }

    @BeforeClass
    public static void beforeClass() {
        mockMongo = new MockMongo(27010);
        mockMongo.start();

        mockMongo2 = new MockMongo(27011);
        mockMongo2.start();
    }

    @AfterClass
    public static void afterClass() {
        mockMongo.stop();
        mockMongo2.stop();
    }

}

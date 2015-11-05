package com.dianping.swallow.producerserver.impl;



import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.packet.PacketType;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.packet.PktProducerGreet;
import com.dianping.swallow.common.internal.packet.PktSwallowPACK;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.SHAUtil;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.producer.exceptions.ServerDaoException;
import com.dianping.swallow.producerserver.AbstractProducerServerTest;

public class ProducerServerForClientTest extends AbstractProducerServerTest{

    @Test
    public void testProducerServerForClient() throws Exception {
    	
    	
        ProducerServerForClient producerServerForClient = new ProducerServerForClient();
        producerServerForClient.setMessageReceiver(messageReceiver);

        int port = 4000;
        producerServerForClient.setPort(port);
        producerServerForClient.setRemoteServiceName("remoteService");

        Assert.assertEquals(port, producerServerForClient.getPort());

        producerServerForClient.start();

        //构造greet
        PktProducerGreet pktProducerGreet = new PktProducerGreet("0.6.0", "Unit Test");
        //构造message
        SwallowMessage swallowMessage = new SwallowMessage();
        swallowMessage.setContent(topicName);
        swallowMessage.setGeneratedTime(new Date());
        swallowMessage.setSourceIp("192.168.32.194");
        swallowMessage.setVersion("0.6.0");
        PktMessage pktMessage = new PktMessage(Destination.topic(topicName), swallowMessage);

        PktSwallowPACK ACK = null;
        //发送greet
        ACK = (PktSwallowPACK) producerServerForClient.sendMessage(pktProducerGreet);

        Assert.assertEquals(PacketType.SWALLOW_P_ACK, ACK.getPacketType());
        Assert.assertEquals(IPUtil.getFirstNoLoopbackIP4Address(), ACK.getShaInfo());

        
        ACK = (PktSwallowPACK) producerServerForClient.sendMessage(pktMessage);
        Assert.assertEquals(PacketType.SWALLOW_P_ACK, ACK.getPacketType());
        Assert.assertEquals(SHAUtil.generateSHA(swallowMessage.getContent()), ACK.getShaInfo());

        replaceExceptionDao();
        ACK = null;
        try {
            ACK = (PktSwallowPACK) producerServerForClient.sendMessage(pktMessage);
        } catch (ServerDaoException e) {
        }
        Assert.assertNull(ACK);
    }
    
    



	@BeforeClass
    public static void beforeClass() {
    }

    @AfterClass
    public static void afterClass() {
    }

}

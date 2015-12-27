package com.dianping.swallow.test.client.producer;

import com.meituan.nuclearmq.client.Producer;
import com.meituan.nuclearmq.client.error.MQException;
import com.meituan.nuclearmq.client.util.UtilCommon;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2015/12/18  下午5:59.
 */
public class NuclearProducerClientTest {

    private static final Logger logger = LogManager.getLogger(NuclearProducerClientTest.class);

    public void produce(String[] args) {
        Producer producer = Producer.Factory.create();
        producer.setAppkey("mtpoiop"); // 1.设置appkey(必须设置)
        producer.setTopic("test_for_shanghai1"); // 2.设置topic(必须设置)
        producer.setIsOnline(true); // 3.设置是否是线上环境
        Future<Integer> future = null;
        try {
            future = producer.start(); // 4.启动生产者
        } catch (MQException e) {
            logger.error("[produce] producer start exception: ", e);
            return;
        }
        while (!future.isDone()) {
            UtilCommon.sleep(500);
        }
        while (true) {
            for (int i = 0; i < 1000; i++) {
                String msg = "hello world nuclear hello world nuclear hello world" + i;
                try {
                    producer.send(msg.getBytes(), 1000); // 5. 同步发送消息
                } catch (MQException e) {
                    logger.error("[produce] send exception.",e);
                }
            }
            logger.info("[produce] 1000 message.");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                logger.error("[[produce] interrupted", e);
            }
        }

    }

    public static void main(String[] args) {
        final NuclearProducerClientTest clientTest = new NuclearProducerClientTest();
        clientTest.produce(null);
    }
}

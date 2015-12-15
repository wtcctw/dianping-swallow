package com.dianping.test.swallow.log4j2;


import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SyncProducerRunner {

    private static Logger logger = LogManager.getLogger(ProducerFactoryImpl.class);

    public static void main(String[] args) {

        try {

            ProducerConfig config = new ProducerConfig();
            ProducerMode mode = ProducerMode.SYNC_MODE;
            config.setMode(mode);
            Producer producer = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("LoadTestTopic-0"),
                    config);
            int j = 0;
            //String msg = new Date() + "message";
            String msg = "11111111message111111111111111111111111111111";
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 20; ++i) {
                sb.append(msg);
            }
            long before = System.currentTimeMillis();
            for (; ; ) {

                j++;
                if (j >= 20000) {
                    System.out.println(System.currentTimeMillis() - before);
                    return;
                }
                producer.sendMessage(sb.toString());
                logger.info(msg);
                //Thread.sleep(400);
                //System.out.println("*****************");
            }
        } catch (Exception e) {
        }
    }
}

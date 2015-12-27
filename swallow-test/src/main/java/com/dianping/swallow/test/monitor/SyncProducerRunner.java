package com.dianping.swallow.test.monitor;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class SyncProducerRunner {

	private static Logger logger = LogManager.getLogger(SyncProducerRunner.class);

	public static void main(String[] args) {

		try {

			ProducerConfig config = new ProducerConfig();
			ProducerMode mode = ProducerMode.SYNC_MODE;
			config.setMode(mode);
			Producer producer = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("LoadTestTopic-0"),
					config);

			for (;;) {

				String msg = new Date() + "message";
				producer.sendMessage(msg);
				logger.info(msg);
				Thread.sleep(400);
				System.out.println("*****************");
			}
		} catch (Exception e) {
		}
	}
}

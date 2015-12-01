package com.dianping.swallow.test.monitor;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import com.dianping.swallow.test.load.AbstractLoadTest;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class ConsumerRunner extends AbstractLoadTest {

	private static int topicCount = 5;
	private static int consumerCount = 10;
	private static int threadPoolSize = 2;
	private static int totalMessageCount = -1;

	private static boolean differentConsumerId = true;

	private String consumerIdPrefix = System.getProperty("consumerIdPrefix");

	public static void main(String[] args) throws Exception {

		if (args.length >= 1) {
			topicCount = Integer.parseInt(args[0]);
		}
		if (args.length >= 2) {
			consumerCount = Integer.parseInt(args[1]);
		}
		if (args.length >= 3) {
			threadPoolSize = Integer.parseInt(args[2]);
		}
		if (args.length >= 4) {
			totalMessageCount = Integer.parseInt(args[3]);
		}

		differentConsumerId = Boolean.parseBoolean(System.getProperty("differentConsumerId"));
		new ConsumerRunner().start();
	}

	@Override
	protected void doStart() {
		if (logger.isInfoEnabled()) {
			logger.info("[doStart][topicCount, consumerCount, threadPoolSize, totalMessageCount, differentConsumerId]"
					+ topicCount + "," + consumerCount + "," + threadPoolSize + "," + totalMessageCount + ","
					+ differentConsumerId);
		}
		startReceiver();
	}

	@Override
	protected boolean isExitOnExecutorsReturn() {

		return false;
	}

	@SuppressWarnings("deprecation")
	private void startReceiver() {

		String rawConsumerId = getConsumerId();

		for (int i = 0; i < topicCount; i++) {
			String topic = getTopicName(topicName, i);
			for (int j = 0; j < consumerCount; j++) {
				ConsumerConfig config = new ConsumerConfig();
				// 以下两项根据自己情况而定，默认是不需要配的
				config.setThreadPoolSize(threadPoolSize);
				config.setRetryCountOnBackoutMessageException(0);

				String consumerId = rawConsumerId;
				if (differentConsumerId) {
					consumerId += "-" + j;
				}
				Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), consumerId,
						config);
				c.setListener(new MessageListener() {
					@Override
					public void onMessage(Message msg) {
						increaseAndGetCurrentCount();
					}
				});
				c.start();
			}
		}
	}

	private String getConsumerId() {

		if (consumerIdPrefix != null) {
			return consumerIdPrefix;
		}

		SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss");
		return "myid-" + format.format(new Date());
	}
}
package com.dianping.swallow.web.monitor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.QPX;

/**
 * @author mengwenchao
 *
 *         2015年4月21日 上午10:38:07
 */
public interface ConsumerDataRetriever extends MonitorDataRetriever {

	List<ConsumerDataPair> getDelayForAllConsumerId(String topic, long start, long end);

	List<ConsumerDataPair> getDelayForAllConsumerId(String topic) throws Exception;

	List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx, long start, long end);

	List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx);

	Map<String, ConsumerDataPair> getServerQpx(QPX qpx, long start, long end);

	Map<String, ConsumerDataPair> getServerQpx(QPX qpx);

	Map<String, Set<String>> getAllTopics();

	public static class ConsumerDataPair {

		private String consumerId;
		private StatsData sendData;
		private StatsData ackData;

		public ConsumerDataPair(String consumerId, StatsData sendData, StatsData ackData) {
			this.consumerId = consumerId;
			this.sendData = sendData;
			this.ackData = ackData;
		}

		public String getConsumerId() {
			return consumerId;
		}

		public StatsData getSendData() {
			return sendData;
		}

		public StatsData getAckData() {
			return ackData;
		}
	}

}

package com.dianping.swallow.common.server.monitor.data.statis;

import java.util.Map.Entry;
import java.util.NavigableMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;

/**
 * @author mengwenchao
 *
 *         2015年5月21日 上午10:40:57
 */
public class ProducerServerDataTest extends AbstractServerDataTest {

	private ProducerAllData producerAllData;

	@Before
	public void beforeProducerServerDataTest() {

		producerAllData = new ProducerAllData();
		prepareData(producerAllData);
		producerAllData.build(QPX.SECOND, startKey, endKey, intervalCount);
	}

	@Test
	public void testRetriever() {

		String server = ips[0];
		String topic = topics[0];
		String ip = ips[0];
		System.out.println(producerAllData.getKeys(new CasKeys()).getClass());

		System.out.println(producerAllData.getValue(new CasKeys(server)).getClass());
		ProducerServerStatisData producerServerStatisData = (ProducerServerStatisData) producerAllData
				.getValue(new CasKeys("total"));
		System.out.println(producerServerStatisData);
		System.out.println(producerAllData.getKeys(new CasKeys(server)).getClass());
		System.out.println(producerAllData.getKeys(new CasKeys(server, topic)).getClass());
		System.out.println(producerAllData.getValue(new CasKeys(server, topic)).getClass());
//		ProducerTopicStatisData producerTopicStatisData = (ProducerTopicStatisData) producerAllData
//				.getValue(new CasKeys(server, "total"));
//		System.out.println(producerTopicStatisData);
		System.out.println(producerAllData.getValue(new CasKeys(server, topic, ip)).getClass());

	}

	@Test
	public void testProducerServerData() {

		int totalCount = (int) ((endKey - startKey) / intervalCount);
		for (String topic : topics) {

			NavigableMap<Long, Long> saveDelay = producerAllData.getDelayForTopic(topic, StatisType.SAVE);
			NavigableMap<Long, Long> saveQpx = producerAllData.getQpxForTopic(topic, StatisType.SAVE);

			expected(saveDelay, totalCount, avergeDelay);
			expected(saveQpx, totalCount, qpsPerUnit * ips.length);
		}

		for (Entry<String, NavigableMap<Long, Long>> entry : producerAllData.getQpxForServers(StatisType.SAVE)
				.entrySet()) {

			String ip = entry.getKey();
			NavigableMap<Long, Long> value = entry.getValue();

			if (logger.isInfoEnabled()) {
				logger.info("[testProducerServerData]" + ip + "," + value);
			}
			expected(entry.getValue(), totalCount, qpsPerUnit * topics.length);
		}

	}

	/**
	 * @param saveDelay
	 * @param totalCount
	 * @param avergeDelay2
	 */
	protected void expected(NavigableMap<Long, Long> data, int totalCount, Long result) {

		Assert.assertEquals(totalCount, data.size());
		for (Long value : data.values()) {

			Assert.assertEquals(result, value);
		}

	}

	public void prepareData(ProducerAllData producerAllData) {

		for (String ip : ips) {

			ProducerMonitorData producerMonitorData = new ProducerMonitorData();
			producerMonitorData.setSwallowServerIp(ip);

			for (Long i = startKey; i <= endKey; i++) {

				producerMonitorData.setCurrentTime(i * AbstractCollector.SEND_INTERVAL * 1000);
				sendData(producerMonitorData, i, ip);
				producerMonitorData.buildTotal();

				try {
					producerAllData
							.add(producerMonitorData.getKey(), (ProducerMonitorData) producerMonitorData.clone());
				} catch (CloneNotSupportedException e) {
					logger.error("[prepareData]", e);
				}
			}

		}

	}

	private ProducerMonitorData sendData(ProducerMonitorData producerMonitorData, Long key, String ip) {

		for (String topic : topics) {

			for (int i = 0; i < qpsPerUnit * AbstractCollector.SEND_INTERVAL; i++) {
				Long current = System.currentTimeMillis();
				producerMonitorData.addData(topic, ip, System.currentTimeMillis(), current - avergeDelay, current);
			}
		}

		return producerMonitorData;
	}
}

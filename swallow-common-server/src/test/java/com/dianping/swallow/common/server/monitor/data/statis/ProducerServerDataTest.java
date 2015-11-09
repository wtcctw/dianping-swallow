package com.dianping.swallow.common.server.monitor.data.statis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import com.dianping.swallow.common.server.monitor.data.Statisable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;
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

		System.out.println(producerAllData.getKeys(new CasKeys(server, topic)));
		System.out.println(producerAllData.getKeys(new CasKeys("total", topic)));
		NavigableMap<Long, Statisable.QpxData> res1 = producerAllData.getQpsValue(new CasKeys("total", topic, ip), StatisType.SAVE);
		NavigableMap<Long, Statisable.QpxData> res2= producerAllData.getQpsValue(new CasKeys("total", topic, ip), StatisType.SEND);
		System.out.println(producerAllData.getQpsValue(new CasKeys("total", topic, ip), StatisType.SAVE));

	}

	@Test
	public void testGetAllDelay() {

		//test merge and getDelayForTopic
		String topic = topics[0];
		NavigableMap<Long, Long> result = producerAllData.getDelayForTopic(topic, StatisType.SAVE);
		NavigableMap<Long, Long> value1 = producerAllData.getDelayValue(new CasKeys("total", topic), StatisType.SAVE);
		Assert.assertEquals(result, value1);
		NavigableMap<Long, Long> result2 = producerAllData.getDelayForTopic(topic, StatisType.SAVE);
		//not change origin data
		Assert.assertEquals(result, result2);

	}

	@Test
	public void testGetAllQps() {

		//test merge and getQpxForTopic
		String topic = topics[0];
		NavigableMap<Long, Statisable.QpxData> result1 = producerAllData.getQpxForTopic(topic, StatisType.SAVE);
		NavigableMap<Long, Statisable.QpxData> result2 = producerAllData.getQpsValue(new CasKeys("total", topic), StatisType.SAVE);
		Assert.assertEquals(result1.size(), result2.size());
		for(Map.Entry<Long, Statisable.QpxData> entry : result1.entrySet()){
			Long key = entry.getKey();
			Statisable.QpxData value1 = entry.getValue();
			Statisable.QpxData value2 = result2.get(key);
			Assert.assertEquals(value1.getQpx(), value2.getQpx());
			Assert.assertEquals(value1.getTotal(), value2.getTotal());
		}
		//not change origin data
		NavigableMap<Long, Statisable.QpxData> result3 = producerAllData.getQpxForTopic(topic, StatisType.SAVE);
		for(Map.Entry<Long, Statisable.QpxData> entry : result1.entrySet()){
			Long key = entry.getKey();
			Statisable.QpxData value1 = entry.getValue();
			Statisable.QpxData value3 = result3.get(key);
			Assert.assertEquals(value1.getQpx(), value3.getQpx());
			Assert.assertEquals(value1.getTotal(), value3.getTotal());
		}
	}

	@Test
	public void testProducerServerData() {

		int totalCount = (int) ((endKey - startKey) / intervalCount);
		for (String topic : topics) {

			NavigableMap<Long, Long> saveDelay = producerAllData.getDelayForTopic(topic, StatisType.SAVE);
			NavigableMap<Long, QpxData> saveQpx = producerAllData.getQpxForTopic(topic, StatisType.SAVE);

			expectedDelay(saveDelay, totalCount, avergeDelay);
			expectedQpx(saveQpx, totalCount, qpsPerUnit * ips.length, qpsPerUnit * ips.length
					* AbstractCollector.SEND_INTERVAL * intervalCount);
		}

		for (Entry<String, NavigableMap<Long, QpxData>> entry : producerAllData.getQpxForServers(StatisType.SAVE)
				.entrySet()) {

			String ip = entry.getKey();
			NavigableMap<Long, QpxData> value = entry.getValue();

			if (logger.isInfoEnabled()) {
				logger.info("[testProducerServerData]" + ip + "," + value);
			}
			expectedQpx(entry.getValue(), totalCount, qpsPerUnit * topics.length, qpsPerUnit * topics.length
					* AbstractCollector.SEND_INTERVAL * intervalCount);
		}

	}

	/**
	 * @param saveDelay
	 * @param totalCount
	 * @param avergeDelay2
	 */
	protected void expectedQpx(NavigableMap<Long, QpxData> data, int totalCount, Long resultQpx, Long resultTotal) {

		Assert.assertEquals(totalCount, data.size());
		for (QpxData value : data.values()) {

			Assert.assertEquals(resultQpx, value.getQpx());
			Assert.assertEquals(resultTotal, value.getTotal());
		}

	}

	/**
	 * @param saveDelay
	 * @param totalCount
	 * @param avergeDelay2
	 */
	protected void expectedDelay(NavigableMap<Long, Long> data, int totalCount, Long result) {

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

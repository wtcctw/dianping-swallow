package com.dianping.swallow.common.server.monitor.data.statis;

import java.util.Map.Entry;
import java.util.NavigableMap;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;

/**
 * @author mengwenchao
 *
 * 2015年5月21日 上午10:40:57
 */
public class ProducerServerDataTest extends AbstractServerDataTest{
	
	private ProducerAllData  producerAllData; 
	
	private Long startKey = 100L, endKey = 400L;
	
	protected String []topics = new String[]{"topic1", "topic2"};
	
	protected String []ips = new String[]{"127.0.0.1", "127.0.0.2"};
	
	protected final long avergeDelay = 50;
	protected final long qpsPerUnit = 10;
	protected final int intervalCount = 6;
	
	
	@Before
	public void beforeProducerServerDataTest(){
		
		producerAllData = new ProducerAllData();
		prepareData(producerAllData);
		producerAllData.build(QPX.SECOND, startKey, endKey, intervalCount, 0);
	}
	
	@Test
	public void testProducerServerData(){
		
		int totalCount = (int) ((endKey - startKey)/intervalCount); 
		for(String topic : topics){
			
			NavigableMap<Long, Long> saveDelay = producerAllData.getSaveDelayForTopic(topic);
			NavigableMap<Long, Long> saveQpx = producerAllData.getSaveQpxForTopic(topic);
			
			expected(saveDelay, totalCount, avergeDelay);
			expected(saveQpx, totalCount, qpsPerUnit * ips.length);
		}
		
		for(Entry<String, NavigableMap<Long, Long>>  entry : producerAllData.getSaveQpxForServers().entrySet()){
			
			String ip = entry.getKey();
			NavigableMap<Long, Long> value = entry.getValue();
			
			if(logger.isInfoEnabled()){
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
	protected void expected(NavigableMap<Long, Long> data, int totalCount,
			Long result) {
		
		Assert.assertEquals(totalCount, data.size());
		for(Long value : data.values()){
			
			Assert.assertEquals(result, value);
		}
		
		
	}

	public void prepareData(ProducerAllData producerAllData){
		
		for(String ip : ips){
			
			ProducerMonitorData producerMonitorData = new ProducerMonitorData();
			producerMonitorData.setSwallowServerIp(ip);

			for(Long i = startKey;i <= endKey ;i++){
				
				producerMonitorData.setCurrentTime(i * AbstractCollector.SEND_INTERVAL * 1000);
				sendData(producerMonitorData, i, ip);
				producerMonitorData.buildTotal();
				
				producerAllData.add(producerMonitorData.getKey(), (ProducerMonitorData)SerializationUtils.clone(producerMonitorData));
				
			}
		}
		
	}

	private ProducerMonitorData sendData(ProducerMonitorData producerMonitorData, Long key, String ip) {
		
		
		for(String topic : topics){
			
				for(int i=0;i< qpsPerUnit * AbstractCollector.SEND_INTERVAL;i++){
					Long current = System.currentTimeMillis();
					producerMonitorData.addData(topic, ip, System.currentTimeMillis(), current - avergeDelay, current);
				}
		}
		
		return producerMonitorData;
	}
}

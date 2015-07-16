package com.dianping.swallow.common.server.monitor.data.statis;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicLong;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerIdData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;

/**
 * @author mengwenchao
 *
 * 2015年5月21日 上午10:40:57
 */
public class ConsumerServerDataTest extends AbstractServerDataTest{
	
	private ConsumerAllData  consumerAllData; 
	
	protected String []consumerIds = {"id1", "id2"};
	
	protected StatisType []supportedTypes = new StatisType[]{StatisType.SEND, StatisType.ACK};   
	
	private AtomicLong messageIdGenerator = new AtomicLong();

	@Before
	public void beforeProducerServerDataTest(){
		
		consumerAllData = new ConsumerAllData();
		prepareData(consumerAllData);
		consumerAllData.build(QPX.SECOND, startKey, endKey, intervalCount);
	}
	
    public static void method3(String fileName, String content) {   
        RandomAccessFile randomFile = null;  
        try {     
            // 打开一个随机访问文件流，按读写方式     
            randomFile = new RandomAccessFile(fileName, "rw");     
            // 文件长度，字节数     
            long fileLength = randomFile.length();     
            // 将写文件指针移到文件尾。     
            randomFile.seek(fileLength);     
            randomFile.writeBytes(content);      
        } catch (IOException e) {     
            e.printStackTrace();     
        } finally{  
            if(randomFile != null){  
                try {  
                    randomFile.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
	
	@Test
	public void testRetriever(){
		
		String server = ips[0];
		String topic = topics[0];
		String consumerId = consumerIds[0];
		String ip = ips[0];
		
		System.out.println(consumerAllData.getKeys(new CasKeys()));
		ConsumerIdStatisData cisd = (ConsumerIdStatisData) consumerAllData.getValue(new CasKeys(server, topic,consumerId));
		
		System.out.println(consumerAllData.getValue(new CasKeys(server, topic)));
	}
	
	@Test
	public void testClean(){
		
		int consumerIdLength = consumerIds.length + 1;
		
		Set<String> ids = consumerAllData.getConsumerIds(topics[0]);
		
		Assert.assertEquals(consumerIdLength, ids.size());
		
		

		consumerAllData.removeBefore(endKey);
		
		ids = consumerAllData.getConsumerIds(topics[0]);
		
		Assert.assertEquals(consumerIdLength, ids.size());


		consumerAllData.removeBefore(endKey + 1);
		ids = consumerAllData.getConsumerIds(topics[0]);
		Assert.assertNull(ids);

		
	}
	
	@Test
	public void testConsumerServerData(){
		
		int totalCount = (int) ((endKey - startKey)/intervalCount);
		
		for(String topic : topics){
			for(StatisType type : supportedTypes){
				
				if(logger.isInfoEnabled()){
					logger.info("[testConsumerServerData]" + type);
				}
			
				NavigableMap<Long, Long> delays = consumerAllData.getDelayForTopic(topic, type);
				NavigableMap<Long, Long> qpxs = consumerAllData.getQpxForTopic(topic, type);
				
				expectedDelay(delays, totalCount, avergeDelay);
				expected(qpxs, totalCount, qpsPerUnit * ips.length * consumerIds.length);

				Map<String, NavigableMap<Long, Long>> allDelay = consumerAllData.getDelayForAllConsumerId(topic, type, false);
				Map<String, NavigableMap<Long, Long>> allQpx = consumerAllData.getQpxForAllConsumerId(topic, type, false);
				
				Assert.assertEquals(consumerIds.length, allDelay.size());
				Assert.assertEquals(consumerIds.length, allQpx.size());
				
				for(Entry<String, NavigableMap<Long, Long>> entry : allDelay.entrySet()){
					
					NavigableMap<Long, Long> consumerDelay = entry.getValue();
					expectedDelay(consumerDelay, totalCount, avergeDelay);
				}
				
				for(Entry<String, NavigableMap<Long, Long>> entry : allQpx.entrySet()){
					
					NavigableMap<Long, Long> consumerQpx = entry.getValue();
					expectedDelay(consumerQpx, totalCount, qpsPerUnit * ips.length);
				}
				
				
			}
			
			
		}
		
		
		
		
	}
	
	private void expectedDelay(NavigableMap<Long, Long> delays,
			int totalCount, long avergeDelay) {
		
		if(logger.isInfoEnabled()){
			logger.info("[expectedDelay]["+avergeDelay+"]" + delays);
		}
		
		Assert.assertEquals(totalCount, delays.size());
		for(Long value : delays.values()){
			
			Long low = (long) (avergeDelay * 0.8);
			Long high = (long) (avergeDelay * 1.2);
			
			assertTrue(value >= low && value <= high);
		}
	}

	protected void expected(NavigableMap<Long, Long> data, int totalCount,
			Long result) {

		if(logger.isInfoEnabled()){
			logger.info("[expected]["+avergeDelay+"]" + data);
		}

		Assert.assertEquals(totalCount, data.size());
		for(Long value : data.values()){
			
			Assert.assertEquals(result, value);
		}
	}

	public void prepareData(ConsumerAllData consumerAllData){
		
		for(String ip : ips){
			
			ConsumerMonitorData consumerMonitorData = new ConsumerMonitorData();
			consumerMonitorData.setSwallowServerIp(ip);
			
			
			for(Long i = startKey;i <= endKey ;i++){
				
				List<Wrapper> datas = new LinkedList<ConsumerServerDataTest.Wrapper>();
				
				consumerMonitorData.setCurrentTime(i * AbstractCollector.SEND_INTERVAL * 1000);
				
				datas.addAll(sendData(consumerMonitorData, i, ip));

				if(logger.isDebugEnabled()){
					logger.debug("[prepareData][total]" + consumerMonitorData);
				}

				System.setProperty(ConsumerIdData.ACK_DELAY_FOR_UNIT_KEY, String.valueOf(avergeDelay));
				ackData(consumerMonitorData, datas);

				consumerMonitorData.buildTotal();
				
				ConsumerMonitorData copy = null;
				try {
					copy = (ConsumerMonitorData) consumerMonitorData.clone();
				} catch (CloneNotSupportedException e) {
					logger.error("[prepareData]", e);
				}

				consumerAllData.add(consumerMonitorData.getKey(), copy);
			}
			
			
		}
		
	}

	private void ackData(ConsumerMonitorData consumerMonitorData, List<Wrapper> datas) {
		
		for(Wrapper wrapper : datas){
			
			consumerMonitorData.addAckData(wrapper.info, wrapper.ip, wrapper.message);
		}
	}

	private List<Wrapper> sendData(ConsumerMonitorData consumerMonitorData, Long key, String ip) {
		
		List<Wrapper> wrappers = new LinkedList<Wrapper>();
		
		for(String topic : topics){
			
			for(String consumerId : consumerIds){
				
				ConsumerInfo consumerInfo = new ConsumerInfo(consumerId, Destination.topic(topic), ConsumerType.DURABLE_AT_LEAST_ONCE);
			
				for(int i=0 ; i< qpsPerUnit * AbstractCollector.SEND_INTERVAL ; i++){
					
					SwallowMessage message = createMessage();
					message.setMessageId(messageIdGenerator.incrementAndGet());
					message.getInternalProperties().put(MessageDAOImpl.SAVE_TIME, String.valueOf(System.currentTimeMillis() - avergeDelay));
					wrappers.add(new Wrapper(consumerInfo, ip, message));
					
					consumerMonitorData.addSendData(consumerInfo, ip, message);
				}
			}
		}
		
		return wrappers;
	}
	
	
	public static class Wrapper{
		
		protected ConsumerInfo  info;
		protected String ip;
		protected SwallowMessage message;

		public Wrapper(ConsumerInfo info, String ip, SwallowMessage message){
			this.info = info;
			this.ip = ip;
			this.message = message;
		}
	}
}

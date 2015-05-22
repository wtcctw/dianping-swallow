package com.dianping.swallow.common.server.monitor.data.structure;

import java.util.NavigableMap;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.MessageInfoStatis;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 上午10:28:00
 */
public class MessageInfoCollectionTest extends AbstractTest{
	
	private MessageInfoStatis messageInfoCollection;
	
	private final Long startKey = 100L, endKey = 200L;
	
	private final Long expectedQpx = 2L, expectedDelay = 10L;
	
	private final int intervalCount = 4;
	
	@Before
	public void beforeMessageInfoCollectionTest(){
		
		messageInfoCollection = new MessageInfoStatis();
		
		prepareData();
	}
	
	
	private void prepareData() {

		MessageInfo info = new MessageInfo();

		for(Long key = startKey; key <= endKey ;key++){
			
			for(int i =0; i< expectedQpx*AbstractCollector.SEND_INTERVAL;i++){
				
				info.addMessage(key, System.currentTimeMillis() - expectedDelay, System.currentTimeMillis());
			}
			messageInfoCollection.add(key, (MessageInfo) SerializationUtils.clone(info));
		}
	}


	@Test
	public void testQpx(){
		
		messageInfoCollection.build(QPX.SECOND, startKey, endKey, intervalCount, 0);
		
		NavigableMap<Long, Long>  qpx = messageInfoCollection.getQpx(StatisType.SAVE);
		
		int size = (int) ((endKey - startKey)/intervalCount);
		Assert.assertEquals(size, qpx.size());
		
		for(Long qp : qpx.values()){
			Assert.assertEquals(expectedQpx, qp);
		}
	}
	
	
	@Test
	public void testInsertLackData(){
		
		int insertCount = 100;
		
		messageInfoCollection.build(QPX.SECOND, startKey, endKey + insertCount, intervalCount, 0);
		
		NavigableMap<Long, Long> qpxs = messageInfoCollection.getQpx(StatisType.SAVE);
		NavigableMap<Long, Long> delays = messageInfoCollection.getDelay(StatisType.SAVE);
		
		int size = (int) ((endKey - startKey + insertCount)/intervalCount);
		Assert.assertEquals(size, qpxs.size());
		Assert.assertEquals(size, delays.size());

		int firstSize = (int) ((endKey - startKey)/intervalCount);
		int count = 0;
		for(Long qpx : qpxs.values()){
			
			if(count < firstSize){
				Assert.assertEquals(expectedQpx, qpx);
			}else{
				Assert.assertEquals((Long)0L, qpx);
			}
			count++;
		}
		
		count  = 0;
		for(Long delay : delays.values()){
			
			if(count < firstSize){
				Assert.assertEquals(expectedDelay, delay);
			}else{
				Assert.assertEquals((Long)0L, delay);
			}
			count++;
		}
		
	}
	
	@Test
	public void testDelay(){
		
		messageInfoCollection.build(QPX.SECOND, startKey, endKey, intervalCount, 0);
		
		NavigableMap<Long, Long>  delays = messageInfoCollection.getDelay(StatisType.SAVE);
		
		int size = (int) ((endKey - startKey)/intervalCount);
		
		Assert.assertEquals(size, delays.size());
		
		for(Long delay : delays.values()){
			
			Assert.assertEquals(expectedDelay, delay);
		}
		
	}
	
}

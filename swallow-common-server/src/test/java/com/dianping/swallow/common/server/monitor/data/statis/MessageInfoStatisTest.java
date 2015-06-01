package com.dianping.swallow.common.server.monitor.data.statis;

import java.util.NavigableMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.MessageInfoStatis;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 上午10:28:00
 */
public class MessageInfoStatisTest extends AbstractTest{
	
	private MessageInfoStatis messageInfoStatis;
	
	private final Long startKey = 100L, endKey = 200L;
	
	private final Long expectedQpx = 2L, expectedDelay = 10L;
	
	private final int intervalCount = 4;
	
	@Before
	public void beforeMessageInfoCollectionTest(){
		
		messageInfoStatis = new MessageInfoStatis();
		
		prepareData();
	}
	
	
	private void prepareData() {

		MessageInfo info = new MessageInfo();

		for(Long key = startKey; key <= endKey ;key++){
			
			for(int i =0; i< expectedQpx*AbstractCollector.SEND_INTERVAL;i++){
				
				Long current = System.currentTimeMillis();
				info.addMessage(key, current - expectedDelay, current);
			}
			try {
				messageInfoStatis.add(key, (MessageInfo) info.clone());
			} catch (CloneNotSupportedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@Test
	public void testMergeDirty() throws CloneNotSupportedException{
		
		int addCount = 100;
		int wrong = 2;
		
		MessageInfo info1 = new MessageInfo();
		MessageInfo info2 = new MessageInfo();
		
		for(Long i = endKey + 1;  i <= endKey + addCount; i++){
			
			add(info1);
			add(info2);
			
			if(i <= endKey + wrong){
				messageInfoStatis.add(i , (MessageInfo)info1.clone());
			}else{
				messageInfoStatis.add(i , (MessageInfo)info1.clone());
				messageInfoStatis.add(i , (MessageInfo)info2.clone());
			}
		}
		
		messageInfoStatis.build(QPX.SECOND, endKey + 1, endKey + addCount, 1);
		
		NavigableMap<Long, Long> qpx = messageInfoStatis.getQpx(StatisType.SAVE);
		NavigableMap<Long, Long> delay = messageInfoStatis.getDelay(StatisType.SAVE);
		
		expect(qpx.headMap(endKey + wrong, true), wrong , 0L);
		expect(qpx.tailMap(endKey + wrong + 1, true), addCount - wrong - 1, expectedQpx * 2);
		
		expect(delay.headMap(endKey + wrong, true), wrong , 0L);
		expect(delay.tailMap(endKey + wrong + 1, true), addCount - wrong -1, expectedDelay);
		
		
	}

	
	private void add(MessageInfo info) {
		
		for(int j=0; j<AbstractCollector.SEND_INTERVAL*expectedQpx ; j++){
			Long current = System.currentTimeMillis();
			info.addMessage(1, current - expectedDelay, current);
		}
	}


	@Test
	public void testAjustBigInterval() throws CloneNotSupportedException{
		
		int addCount = 20;
		addWrongData(addCount);
		
		messageInfoStatis.build(QPX.SECOND, endKey , endKey + addCount, intervalCount);
		
		expect(messageInfoStatis.getDelay(StatisType.SAVE), addCount/intervalCount, expectedDelay);
		expect(messageInfoStatis.getQpx(StatisType.SAVE), addCount/intervalCount, expectedQpx);
		
		
	}
	

	@Test
	public void testAjustOneInterval() throws CloneNotSupportedException{
		
		int addCount = 20;
		addWrongData(addCount);
		
		messageInfoStatis.build(QPX.SECOND, endKey, endKey + addCount, 1);
		NavigableMap<Long, Long> qpx = messageInfoStatis.getQpx(StatisType.SAVE);
		
		expect(qpx.headMap(endKey + 1, true), 2, 0L);
		expect(qpx.tailMap(endKey + 2, true), addCount - 2, expectedQpx);

		NavigableMap<Long, Long> delay = messageInfoStatis.getDelay(StatisType.SAVE);
		
		expect(delay.headMap(endKey + 1, true), 2, 0L);
		expect(delay.tailMap(endKey + 2, true), addCount - 2, expectedDelay);

	}
	
	

	private void addWrongData(int addCount) throws CloneNotSupportedException {
		
		MessageInfo wrongInfo = new MessageInfo();
		
		for(int i=0 ; i < addCount; i++){
			
			add(wrongInfo);
			
			messageInfoStatis.add(endKey + i + 1, (MessageInfo) wrongInfo.clone());
		}
	}


	private void expect(NavigableMap<Long, Long> data, int expectCount, Long expectedData) {
		
		if(logger.isInfoEnabled()){
			logger.info(data);
		}
		
		Assert.assertEquals(expectCount, data.size());
		for(Long value : data.values()){
			Assert.assertEquals(expectedData, value);
		}
		
	}


	@Test
	public void testQpx(){
		
		messageInfoStatis.build(QPX.SECOND, startKey, endKey, intervalCount);
		
		NavigableMap<Long, Long>  qpx = messageInfoStatis.getQpx(StatisType.SAVE);
		
		int size = (int) ((endKey - startKey)/intervalCount);
		Assert.assertEquals(size, qpx.size());
		
		for(Long qp : qpx.values()){
			Assert.assertEquals(expectedQpx, qp);
		}
	}
	
	
	@Test
	public void testInsertLackData(){
		
		int insertCount = 100;
		
		messageInfoStatis.build(QPX.SECOND, startKey, endKey + insertCount, intervalCount);
		
		NavigableMap<Long, Long> qpxs = messageInfoStatis.getQpx(StatisType.SAVE);
		NavigableMap<Long, Long> delays = messageInfoStatis.getDelay(StatisType.SAVE);
		
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
		
		messageInfoStatis.build(QPX.SECOND, startKey, endKey, intervalCount);
		
		NavigableMap<Long, Long>  delays = messageInfoStatis.getDelay(StatisType.SAVE);
		
		int size = (int) ((endKey - startKey)/intervalCount);
		
		Assert.assertEquals(size, delays.size());
		
		for(Long delay : delays.values()){
			
			Assert.assertEquals(expectedDelay, delay);
		}
	}
	
}

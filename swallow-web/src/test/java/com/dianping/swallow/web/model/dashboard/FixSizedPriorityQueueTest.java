package com.dianping.swallow.web.model.dashboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.dianping.swallow.web.dashboard.comparator.AccuComparator;
import com.dianping.swallow.web.dashboard.comparator.AckComparator;
import com.dianping.swallow.web.dashboard.comparator.ConprehensiveComparator;
import com.dianping.swallow.web.dashboard.comparator.SendComparator;
import com.dianping.swallow.web.dashboard.model.Entry;
import com.dianping.swallow.web.dashboard.model.FixSizedPriorityQueue;

public class FixSizedPriorityQueueTest {
	
	private List<Comparator<Entry>> list = new ArrayList<Comparator<Entry>>();
	
	private List<Entry> entrys = new ArrayList<Entry>();
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Before
	public void setUp() throws Exception {
		
		generateComparator();
		generateEntrys();
	}
	
	private void generateComparator(){
		
		list.add(new ConprehensiveComparator());
		list.add(new SendComparator());
		list.add(new AckComparator());
		list.add(new AccuComparator());
	}
	
	private void generateEntrys(){
		
		Random random = new Random();
		for (int i = 0; i < 20; ++i) {
			Entry e = new Entry();
			float rNum1 = random.nextFloat() * 100;
			float rNum2 = random.nextFloat() * 100;
			float rNum3 = random.nextFloat() * 100;
			e.setNumAlarm(1);
			e.setNormalizedAccu(rNum1);
			e.setNormalizedAckDelay(rNum2);
			e.setNormalizedSendDelay(rNum3);
			entrys.add(e);
			if(logger.isInfoEnabled()){
				logger.info(e.toString());
			}
		}
	}

	@Test
	public void test() {
		
		for(int j = 0; j < 4; ++j){

			final FixSizedPriorityQueue pq = new FixSizedPriorityQueue(10, list.get(j));
			for (int i = 0; i < 20; ++i) {
				pq.add(entrys.get(i));
			}
	
			float f0;
			float f1 = Float.MAX_VALUE;
			for (Entry item : pq.sortedList()) {
				f0 = f1;
				if(j == 0){
					f1 = item.getNormalizedAccu() + item.getNormalizedAckDelay()
							+ item.getNormalizedSendDelay();
				}else if(j == 1){
					f1 = item.getNormalizedSendDelay();
				}else if(j == 2){
					f1 = item.getNormalizedAckDelay();
				}else{
					f1 = item.getNormalizedAccu();
				}
				Assert.assertTrue( (f0 - f1) >= 0L);
				if(logger.isInfoEnabled()){
					logger.info("score: " + f1);
				}
			}
			if(logger.isInfoEnabled()){
				logger.info("\n-----------------------------------------------------");
			}
		}
	}

}

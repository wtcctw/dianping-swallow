package com.dianping.swallow.web.model.stats;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.Event;
import com.dianping.swallow.web.model.event.EventFactoryImpl;

public class ProducerTopicStatsDataTest extends MockTest {
	
	@Spy
	private EventFactoryImpl eventFactory;

	@Mock
	private EventReporter eventReporter;

	private ProducerTopicStatsData producerTopicStatsData;

	@Before
	public void before() throws Exception {
		producerTopicStatsData = new ProducerTopicStatsData();
		producerTopicStatsData.setId("127.0.0.1");
		producerTopicStatsData.setTimeKey(11111L);
		producerTopicStatsData.setQps(30);
		producerTopicStatsData.setDelay(10000);
		producerTopicStatsData.setEventFactory(eventFactory);
		producerTopicStatsData.setEventReporter(eventReporter);
		Mockito.doNothing().when(eventReporter).report(Mockito.any(Event.class));
	}
	
	@Test
	public void checkTest() {
		
		Assert.assertFalse(producerTopicStatsData.checkQpsPeak(10));
		Assert.assertTrue(producerTopicStatsData.checkQpsPeak(30));
		Assert.assertTrue(producerTopicStatsData.checkQpsPeak(40));
		
		Assert.assertFalse(producerTopicStatsData.checkQpsValley(40));
		Assert.assertTrue(producerTopicStatsData.checkQpsValley(30));
		Assert.assertTrue(producerTopicStatsData.checkQpsValley(10));
		

		Assert.assertFalse(producerTopicStatsData.checkQpsFlu(10, 1, 10));
		Assert.assertTrue(producerTopicStatsData.checkQpsFlu(30, 20, 10));
		Assert.assertTrue(producerTopicStatsData.checkQpsFlu(10, 20, 2));
		Assert.assertFalse(producerTopicStatsData.checkQpsFlu(10, 90, 2));
		Assert.assertTrue(producerTopicStatsData.checkQpsFlu(10, 60, 3));
		
		Assert.assertFalse(producerTopicStatsData.checkDelay(5));
		Assert.assertTrue(producerTopicStatsData.checkDelay(10));
		Assert.assertTrue(producerTopicStatsData.checkDelay(11));

	}
}

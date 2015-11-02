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

public class ProducerServerStatsDataTest extends MockTest {
	
	@Spy
	private EventFactoryImpl eventFactory;

	@Mock
	private EventReporter eventReporter;

	private ProducerServerStatsData producerServerStatsData;

	@Before
	public void before() throws Exception {
		producerServerStatsData = new ProducerServerStatsData();
		producerServerStatsData.setIp("127.0.0.1");
		producerServerStatsData.setTimeKey(11111L);
		producerServerStatsData.setQps(30);
		producerServerStatsData.setDelay(10000);
		producerServerStatsData.setEventFactory(eventFactory);
		producerServerStatsData.setEventReporter(eventReporter);
		Mockito.doNothing().when(eventReporter).report(Mockito.any(Event.class));
	}
	
	@Test
	public void checkTest() {
		
		Assert.assertFalse(producerServerStatsData.checkQpsPeak(10));
		Assert.assertTrue(producerServerStatsData.checkQpsPeak(30));
		Assert.assertTrue(producerServerStatsData.checkQpsPeak(40));
		
		Assert.assertFalse(producerServerStatsData.checkQpsValley(40));
		Assert.assertTrue(producerServerStatsData.checkQpsValley(30));
		Assert.assertTrue(producerServerStatsData.checkQpsValley(10));

	}
	
}

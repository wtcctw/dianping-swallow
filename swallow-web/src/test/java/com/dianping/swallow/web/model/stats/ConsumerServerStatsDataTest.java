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

public class ConsumerServerStatsDataTest extends MockTest {

	@Spy
	private EventFactoryImpl eventFactory;

	@Mock
	private EventReporter eventReporter;

	private ConsumerServerStatsData consumerServerStatsData;

	@Before
	public void before() throws Exception {
		consumerServerStatsData = new ConsumerServerStatsData();
		consumerServerStatsData.setIp("127.0.0.1");
		consumerServerStatsData.setTimeKey(11111L);
		consumerServerStatsData.setSendQps(30);
		consumerServerStatsData.setSendDelay(10000);
		consumerServerStatsData.setAckQps(30);
		consumerServerStatsData.setAckDelay(10000);
		consumerServerStatsData.setEventFactory(eventFactory);
		consumerServerStatsData.setEventReporter(eventReporter);
		Mockito.doNothing().when(eventReporter).report(Mockito.any(Event.class));
	}
	
	@Test
	public void checkTest() {
		
		Assert.assertFalse(consumerServerStatsData.checkSendQpsPeak(10));
		Assert.assertTrue(consumerServerStatsData.checkSendQpsPeak(30));
		Assert.assertTrue(consumerServerStatsData.checkSendQpsPeak(40));
		
		Assert.assertFalse(consumerServerStatsData.checkSendQpsValley(40));
		Assert.assertTrue(consumerServerStatsData.checkSendQpsValley(30));
		Assert.assertTrue(consumerServerStatsData.checkSendQpsValley(10));
		
		Assert.assertFalse(consumerServerStatsData.checkAckQpsPeak(10));
		Assert.assertTrue(consumerServerStatsData.checkAckQpsPeak(30));
		Assert.assertTrue(consumerServerStatsData.checkAckQpsPeak(40));
		
		Assert.assertFalse(consumerServerStatsData.checkAckQpsValley(40));
		Assert.assertTrue(consumerServerStatsData.checkAckQpsValley(30));
		Assert.assertTrue(consumerServerStatsData.checkAckQpsValley(10));

	}
		
}

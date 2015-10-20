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

public class ConsumerIdStatsDataTest extends MockTest {

	@Spy
	private EventFactoryImpl eventFactory;

	@Mock
	private EventReporter eventReporter;

	private ConsumerIdStatsData consumerIdStatsData;

	@Before
	public void before() throws Exception {
		consumerIdStatsData = new ConsumerIdStatsData();
		consumerIdStatsData.setTopicName("topic_0");
		consumerIdStatsData.setTimeKey(111111L);
		consumerIdStatsData.setConsumerId("consumerId_0");
		consumerIdStatsData.setSendQps(30);
		consumerIdStatsData.setSendDelay(10000);
		consumerIdStatsData.setAccumulation(10);
		consumerIdStatsData.setAckQps(30);
		consumerIdStatsData.setAckDelay(10000);
		
		consumerIdStatsData.setEventFactory(eventFactory);
		consumerIdStatsData.setEventReporter(eventReporter);
		Mockito.doNothing().when(eventReporter).report(Mockito.any(Event.class));
	}

	@Test
	public void checkTest() {
		
		Assert.assertFalse(consumerIdStatsData.checkSendQpsPeak(10));
		Assert.assertTrue(consumerIdStatsData.checkSendQpsPeak(30));
		Assert.assertTrue(consumerIdStatsData.checkSendQpsPeak(40));

		Assert.assertFalse(consumerIdStatsData.checkSendQpsValley(40));
		Assert.assertTrue(consumerIdStatsData.checkSendQpsValley(30));
		Assert.assertTrue(consumerIdStatsData.checkSendQpsValley(10));

		Assert.assertFalse(consumerIdStatsData.checkSendQpsFlu(10, 1, 10));
		Assert.assertTrue(consumerIdStatsData.checkSendQpsFlu(30, 20, 10));
		Assert.assertTrue(consumerIdStatsData.checkSendQpsFlu(10, 20, 2));
		Assert.assertFalse(consumerIdStatsData.checkSendQpsFlu(10, 90, 2));
		Assert.assertTrue(consumerIdStatsData.checkSendQpsFlu(10, 60, 3));

		Assert.assertFalse(consumerIdStatsData.checkSendDelay(5));
		Assert.assertTrue(consumerIdStatsData.checkSendDelay(10));
		Assert.assertTrue(consumerIdStatsData.checkSendDelay(11));

		Assert.assertFalse(consumerIdStatsData.checkSendAccu(5));
		Assert.assertTrue(consumerIdStatsData.checkSendAccu(10));
		Assert.assertTrue(consumerIdStatsData.checkSendAccu(11));

		Assert.assertFalse(consumerIdStatsData.checkAckQpsPeak(10));
		Assert.assertTrue(consumerIdStatsData.checkAckQpsPeak(30));
		Assert.assertTrue(consumerIdStatsData.checkAckQpsPeak(40));

		Assert.assertFalse(consumerIdStatsData.checkAckQpsValley(40));
		Assert.assertTrue(consumerIdStatsData.checkAckQpsValley(30));
		Assert.assertTrue(consumerIdStatsData.checkAckQpsValley(10));

		Assert.assertFalse(consumerIdStatsData.checkAckQpsFlu(10, 1, 10));
		Assert.assertTrue(consumerIdStatsData.checkAckQpsFlu(30, 20, 10));
		Assert.assertTrue(consumerIdStatsData.checkAckQpsFlu(10, 20, 2));
		Assert.assertFalse(consumerIdStatsData.checkAckQpsFlu(10, 90, 2));
		Assert.assertTrue(consumerIdStatsData.checkAckQpsFlu(10, 60, 3));

		Assert.assertFalse(consumerIdStatsData.checkAckDelay(5));
		Assert.assertTrue(consumerIdStatsData.checkAckDelay(10));
		Assert.assertTrue(consumerIdStatsData.checkAckDelay(11));

	}

}

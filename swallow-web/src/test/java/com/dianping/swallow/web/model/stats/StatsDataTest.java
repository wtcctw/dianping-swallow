package com.dianping.swallow.web.model.stats;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * @author qiyin
 *
 * 2015年8月5日 上午10:00:34
 */
@RunWith(Suite.class)
@SuiteClasses({ ConsumerIdStatsDataTest.class, ConsumerServerStatsDataTest.class, ProducerServerStatsDataTest.class,
		ProducerTopicStatsDataTest.class })
public class StatsDataTest {

}

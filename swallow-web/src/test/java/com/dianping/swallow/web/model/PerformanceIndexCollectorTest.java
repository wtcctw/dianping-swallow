package com.dianping.swallow.web.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.dianping.swallow.web.monitor.collector.PerformanceIndexCollector;


/**
 * @author mingdongli
 *
 * 2015年9月16日下午2:59:41
 */

/**
 * 
		load=0.0511833, disk=91.2801, catalog=团购消息队列, qps=1946, ip=10.1.6.31:21018,10.1.6.31:27018
		load=0.0165813, disk=25.7588, catalog=Swallow01消息队列, qps=545, ip=10.1.115.11:27018,10.1.115.12:27018
		load=0.0165813, disk=25.7588,  catalog=搜索消息队列, qps=545, ip=10.1.115.11:27017,10.1.115.12:27017
		load=0.03492, disk=33.5539, catalog=下单消息队列, qps=34, ip=10.1.101.155:27018,10.1.101.157:27018
		load=0.0371833, disk=87.4528, catalog=交易消息队列, qps=3835, ip=10.1.6.186:27017,10.1.6.188:27017
		load=0.03492, disk=33.5539, catalog=缓存消息队列, qps=4221, ip=10.1.101.155:21017,10.1.101.157:27017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class PerformanceIndexCollectorTest {
	
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private PerformanceIndexCollector performanceIndexCollector;

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void test() {
		
		try {
			Thread.sleep(3000);
			String bestConsumerServer = performanceIndexCollector.getBestConsumerServer();
			String bestMongo = performanceIndexCollector.getBestMongo();
			String searchMongo = performanceIndexCollector.getSearchMongo();
			
			System.out.println(bestConsumerServer);
			System.out.println(bestMongo);
			System.out.println(searchMongo);

			assertEquals(bestConsumerServer, "192.168.78.220:8081,192.168.79.100:8082");
			assertEquals(bestMongo, "10.1.115.11:27018,10.1.115.12:27018");
			assertEquals(searchMongo, "10.1.115.11:27017,10.1.115.12:27017");
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

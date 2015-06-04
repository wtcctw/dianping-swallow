package com.dianping.swallow.web.monitor.impl;

import java.util.NavigableMap;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.web.monitor.impl.DefaultAccumulationRetriever.ConsumerIdAccumulation;

/**
 * @author mengwenchao
 *
 * 2015年6月4日 下午6:39:56
 */
public class ConsumerIdAccumulationTest {

	
	@SuppressWarnings("deprecation")
	@Test
	public void tesAjust(){
		
		ConsumerIdAccumulation data = new ConsumerIdAccumulation();
		
		data.lastInsertTime =  5000; 
		
		data.add(1L, 1L);
		
		data.add(13L, 1L);

		data.add(19L, 1L);

		NavigableMap<Long, Long>  result =  data.getAccumulations(6);

		Assert.assertEquals(4, result.size());

		Assert.assertTrue(result.get(7L) == 0L);
		
	}
}

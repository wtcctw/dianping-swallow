package com.dianping.swallow.web.monitor.impl;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.web.monitor.impl.DefaultAccumulationRetriever.TopicAccumulation;

/**
 * @author mengwenchao
 *
 * 2015年6月4日 下午7:14:22
 */
public class TopicAccumulationTest {

	@Test
	public void testRetain(){
		
		TopicAccumulation accu = new TopicAccumulation();
		
		accu.addConsumerId("id1", 0);
		accu.addConsumerId("id2", 0);
		accu.addConsumerId("id3", 0);
		
		Set<String> ids = new HashSet<String>();
		
		ids.add("id2");
		ids.add("id3");
		accu.retain(ids);
		
		Assert.assertEquals(2, accu.consumers().size());

		Set<String> retain = accu.consumers().keySet();
		
		Assert.assertTrue(retain.contains("id2"));
		Assert.assertTrue(retain.contains("id3"));

	}
}

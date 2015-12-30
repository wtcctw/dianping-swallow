package com.dianping.swallow.common.internal.whitelist;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.impl.lion.LionDynamicConfig;

/**
 * @author mengwenchao
 *
 * 2015年9月11日 下午3:48:36
 */
public class TopicWhiteListTest extends AbstractTest{
	
	private TopicWhiteList topicWhiteList;
	
	
	@Before
	public void beforeTopicWhiteListTest(){
		
		 topicWhiteList = new TopicWhiteList();
		 topicWhiteList.setLionDynamicConfig(new  LionDynamicConfig("none"));
		
	}
	
	
	@Test
	public void testGetWhiteList(){
		
		String topics = "a,b, c;d;";
		
		Set<String> whiteList = topicWhiteList.getWhiteList(topics);
		
		Assert.assertEquals(4, whiteList.size());
		
	}
	
	@Test
	public void simpleTest() throws InterruptedException{
		
		topicWhiteList.init();
		
	}

}

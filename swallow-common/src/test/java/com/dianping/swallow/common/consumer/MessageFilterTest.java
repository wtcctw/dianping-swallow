package com.dianping.swallow.common.consumer;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年8月17日 下午4:58:20
 */
public class MessageFilterTest {
	
	@Test
	public void testEquals(){
		
		MessageFilter filter1 = MessageFilter.createInSetMessageFilter("type1");
		MessageFilter filter2 = MessageFilter.createInSetMessageFilter("type1");
		
		Assert.assertEquals(filter1, filter2);

		filter1 = MessageFilter.createInSetMessageFilter("type1", "type2");
		filter2 = MessageFilter.createInSetMessageFilter("type1");
		Assert.assertNotEquals(filter1, filter2);

	}

}

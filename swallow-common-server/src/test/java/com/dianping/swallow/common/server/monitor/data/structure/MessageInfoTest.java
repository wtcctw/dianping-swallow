package com.dianping.swallow.common.server.monitor.data.structure;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.codec.JsonBinder;

/**
 * @author mengwenchao
 *
 * 2015年5月31日 下午3:31:48
 */
public class MessageInfoTest extends AbstractTest{
	
	@Test
	public void testJson(){
		
		MessageInfo info = new MessageInfo();
		info.addMessage(1, 0, 30);
		info.markDirty();
		
		String json = JsonBinder.getNonEmptyBinder().toJson(info);
		
		if(logger.isInfoEnabled()){
			logger.info("[ajust]" + json);
		}
		Assert.assertEquals("{\"totalDelay\":30,\"total\":1,\"isDirty\":true}", json);
		MessageInfo copy = JsonBinder.getNonEmptyBinder().fromJson(json, info.getClass());
		
		Assert.assertEquals(info, copy);
	}
	

}

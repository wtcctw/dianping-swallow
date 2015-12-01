package com.dianping.swallow.common.internal;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.netty.NettyHeapUtil;


/**
 * @author mengwenchao
 *
 * 2015年9月6日 下午7:12:03
 */
public class NettyHeapUtilTest extends AbstractTest{
	
	@Test
	public void test(){
		
		Assert.assertEquals(10, NettyHeapUtil.directArenaCount(16, 8 * 1024, 10));
		
		Assert.assertEquals(10, NettyHeapUtil.heapArenaCount(16, 8 * 1024, 10));
		
		logger.error("just test!!!");
		
	}

}

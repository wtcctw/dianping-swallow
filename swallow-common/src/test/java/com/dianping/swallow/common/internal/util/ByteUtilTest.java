package com.dianping.swallow.common.internal.util;

import org.junit.Before;
import org.junit.Test;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.AbstractTest;

/**
 * @author mengwenchao
 *
 *         2015年12月15日 下午5:42:34
 */
public class ByteUtilTest extends AbstractTest {
	
	private byte []data;
	
	@Before
	public void before(){
		
		data = new byte[256];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (0xFF & i);
		}
	}

	@Test
	public void testLog() {
		
		String result = ByteUtil.toHexString(data, 3);
		logger.info(result);
	}
	
	@Test
	public void testCat(){
		
		for(int i=0; i < 100;i++){
			
			Transaction t = Cat.newTransaction("test", "test");
			t.addData("key", ByteUtil.toHexString(data, 3));
			t.complete();
		}		
		sleep(60000);
	}

}

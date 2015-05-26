package com.dianping.swallow.common.internal.util;


import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年4月27日 上午11:55:07
 */
public class StringUtilsTEst {
	
	@Test
	public void testSplitByComma(){

		String buff[]= {
				"a,b",
				" a,b ",
				" a ,b ",
				" a, b ",
				" a , b ",
		};

		for(String data : buff){
			List<String> result = StringUtils.splitByComma(data);
			judge(result);
		}
		
		
	}

	private void judge(List<String> result) {
		Assert.assertEquals("a", result.get(0));
		Assert.assertEquals("b", result.get(1));
	}

}

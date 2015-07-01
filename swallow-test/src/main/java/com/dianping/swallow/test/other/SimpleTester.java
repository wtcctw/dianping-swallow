package com.dianping.swallow.test.other;

import java.util.Date;

import org.bson.types.BSONTimestamp;
import org.junit.Test;

import com.dianping.swallow.common.internal.util.MongoUtils;

/**
 * @author mengwenchao
 *
 * 2015年5月16日 上午11:40:37
 */
public class SimpleTester {

	@Test
	public void testDate(){
		
		String tmp = "123";
		
		printTime(1);
	}

	private void printTime(int time) {
		
		BSONTimestamp stamp = new BSONTimestamp(1435663543, 667);
		System.out.println(MongoUtils.BSONTimestampToLong(stamp));
		
	}
}

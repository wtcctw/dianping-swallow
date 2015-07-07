package com.dianping.swallow.test.other;

import org.bson.types.BSONTimestamp;
import org.junit.Test;

import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.util.MongoUtils;

/**
 * @author mengwenchao
 *
 * 2015年5月16日 上午11:40:37
 */
public class SimpleTester {

	@Test
	public void testDate(){

		System.out.println(40000 * AbstractSwallowConfig.MILLION);
	}

	private void printTime(int time) {
		
		BSONTimestamp stamp = new BSONTimestamp(1435663543, 667);
		System.out.println(MongoUtils.BSONTimestampToLong(stamp));
	}
}

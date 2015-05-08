package com.dianping.swallow.test.other;

import org.bson.types.BSONTimestamp;
import org.junit.Test;

import com.dianping.swallow.common.internal.util.MongoUtils;

/**
 * @author mengwenchao
 *
 * 2015年5月7日 下午9:47:06
 */
public class TimeStampUtil {

	@Test
	public void testTimeStamp(){
//		BSONTimestamp timestamp = MongoUtils.longToBSONTimestamp(6146147335001870685L);
		BSONTimestamp timestamp = MongoUtils.longToBSONTimestamp(6146147335001870739L);
		
		System.out.println(timestamp.getTime() + "," + timestamp.getInc());
	}
}

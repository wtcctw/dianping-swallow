package com.dianping.swallow.test.other;

import java.text.ParseException;
import java.util.Date;

import org.bson.types.BSONTimestamp;
import org.junit.Test;

import com.dianping.swallow.common.internal.util.DateUtils;
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
		
		print(6148565019337424909L);
//		print(6148547951137390594L);
		
	}
	
	
	@Test
	public void testMongo(){
		
		Long cur = MongoUtils.getLongByCurTime();
		print(cur);
	}
	
	
	@Test
	public void testTimeStampToId() throws ParseException{
		
		Date date = DateUtils.fromSimpleFormat("20150706134900");
		System.out.println(date.getTime()/5000L);
		
	}

	private void print(Long id) {
		
		BSONTimestamp timestamp = MongoUtils.longToBSONTimestamp(id);
		System.out.println(timestamp.getTime() + "," + timestamp.getInc());
		System.out.println(new Date(timestamp.getTime()*1000L));
		
	}
}

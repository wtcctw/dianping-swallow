package com.dianping.swallow.test.man.mongo;

import java.text.ParseException;

import com.dianping.swallow.common.internal.util.DateUtils;

/**
 * @author mengwenchao
 *
 * 2015年5月21日 下午5:41:02
 */
public class MessageUtil extends AbstractMongoUtil{
	
	
	
	public static void main(String []argc) throws ParseException{
		
		new MessageUtil().start();
		
		System.out.println(DateUtils.fromSimpleFormat("20150515000000").getTime()/1000);
		System.out.println(DateUtils.fromSimpleFormat("20150516000000").getTime()/1000);
		System.out.println(DateUtils.fromSimpleFormat("20150517000000").getTime()/1000);
		System.out.println(DateUtils.fromSimpleFormat("20150518000000").getTime()/1000);
		System.out.println(DateUtils.fromSimpleFormat("20150519000000").getTime()/1000);
		System.out.println(DateUtils.fromSimpleFormat("20150520000000").getTime()/1000);
	}

}

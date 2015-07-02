package com.dianping.swallow.common.internal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mengwenchao
 *
 * 2015年4月25日 下午12:20:48
 */
public class DateUtils {
	

    private static SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S");

    private static SimpleDateFormat formatNoMili = new SimpleDateFormat("yyyyMMddHHmmss");

	public static String toPrettyFormat(long date){
		
		return toPrettyFormat(new Date(date));
	}

	public static  Date fromSimpleFormat(String time) throws ParseException{
		
		return formatNoMili.parse(time);
	}
	
	private static String toPrettyFormat(Date date) {
		
		return format.format(date);
	}
	
	public static String current(){
		return toPrettyFormat(new Date());
	}
}

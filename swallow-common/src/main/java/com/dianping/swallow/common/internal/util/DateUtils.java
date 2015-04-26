package com.dianping.swallow.common.internal.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mengwenchao
 *
 * 2015年4月25日 下午12:20:48
 */
public class DateUtils {
	

    private static SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,S");

	public static String toPrettyFormat(long date){
		return toPrettyFormat(new Date(date));
	}

	private static String toPrettyFormat(Date date) {
		return format.format(date);
	}
}

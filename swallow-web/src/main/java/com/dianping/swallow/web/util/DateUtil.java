package com.dianping.swallow.web.util;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月1日 下午11:20:13
 */
public class DateUtil {

	private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT+8:00");

	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String getDateFormat(Date date, String pattern, TimeZone timeZone) {
		return DateFormatUtils.format(date, pattern, timeZone);
	}

	public static String getDefaulFormat() {
		return DateFormatUtils.format(new Date(), DEFAULT_DATE_PATTERN, DEFAULT_TIMEZONE);
	}
}

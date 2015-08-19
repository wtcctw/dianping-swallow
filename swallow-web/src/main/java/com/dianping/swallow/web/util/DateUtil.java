package com.dianping.swallow.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author qiyin
 *
 *         2015年8月1日 下午11:20:13
 */
public class DateUtil {

	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("GMT+8:00");

	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String getDateFormat(Date date, String pattern, TimeZone timeZone) {
		return DateFormatUtils.format(date, pattern, timeZone);
	}

	public static String getDefaulFormat() {
		return DateFormatUtils.format(new Date(), DEFAULT_DATE_PATTERN, DEFAULT_TIMEZONE);
	}

	public static Date convertStrToDate(String strDateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date result = sdf.parse(strDateTime);
			return result;
		} catch (ParseException e) {
			logger.info("[convertStrToDate] data tranform failed.", e);
			return new Date();
		}
	}

	public static int getCurrentHour() {
		return Calendar.getInstance(DEFAULT_TIMEZONE).get(Calendar.HOUR_OF_DAY);
	}
	
	public static long ConvertToTimestamp(Date dateTime){
		return dateTime.getTime();
	}


}

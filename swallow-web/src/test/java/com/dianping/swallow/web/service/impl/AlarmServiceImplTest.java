package com.dianping.swallow.web.service.impl;

import org.junit.BeforeClass;
import org.junit.Test;

import com.dianping.swallow.web.dao.AlarmDao;
import com.dianping.swallow.web.dao.impl.DefaultAlarmDao;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.service.HttpService;

/**
 * 
 * @author qiyin
 *
 */
public class AlarmServiceImplTest {

	static AlarmServiceImpl alarmService = null;

	@BeforeClass
	public static void beforeClass() {
		alarmService = new AlarmServiceImpl();
		HttpService httpService = new HttpServiceImpl();
		alarmService.setHttpService(httpService);
		AlarmDao alarmDao = new DefaultAlarmDao();
		alarmService.setAlarmDao(alarmDao);
	}

	@Test
	public void sendSmsTest() {
		alarmService.sendSms("13162757679", "test", "test", AlarmType.CRITICAL);
	}

	@Test
	public void sendMailTest() {
		alarmService.sendMail("qi.yin@dianping.com", "test", "test", AlarmType.CRITICAL);
	}

	@Test
	public void sendWeiXinTest() {
		alarmService.sendWeixin("qi.yin@dianping.com", "test", "test", AlarmType.CRITICAL);
	}

}

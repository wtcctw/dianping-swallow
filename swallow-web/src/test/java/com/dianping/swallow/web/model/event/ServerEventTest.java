package com.dianping.swallow.web.model.event;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.model.alarm.AlarmLevelType;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;

/**
 * 
 * @author qiyin
 *
 *         2015年8月10日 下午5:43:51
 */
public class ServerEventTest extends MockTest {

	ServerEvent serverEvent = null;
	AlarmMeta alarmMeta = null;

	@Before
	public void before() throws Exception {
		serverEvent = new ServerEvent();
		initAlarmMeta();
	}

	private void initAlarmMeta() {
		alarmMeta = new AlarmMeta();
		alarmMeta.setMetaId(1021);
		alarmMeta.setType(AlarmType.PRODUCER_SERVER_PIGEON_SERVICE);
		alarmMeta.setLevelType(AlarmLevelType.GENERAL);
		alarmMeta.setIsSmsMode(true);
		alarmMeta.setIsWeiXinMode(true);
		alarmMeta.setIsMailMode(true);
		alarmMeta.setIsSendSwallow(false);
		alarmMeta.setIsSendBusiness(true);
		alarmMeta.setAlarmTitle("消费端确认延时告警");
		alarmMeta
				.setAlarmTemplate("消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}确认延时{currentValue}延时大于阈值{expectedValue}(s)。");
		alarmMeta.setAlarmDetail("");
		alarmMeta.setMaxTimeSpan(120);
		alarmMeta.setDaySpanBase(10);
		alarmMeta.setNightSpanBase(15);
		alarmMeta.setCreateTime(new Date());
		alarmMeta.setUpdateTime(new Date());

	}

	//@Test
	public void isAlarmTest() {
		for (int i = 0; i < 21; i++) {
			if (i == 0) {
				Assert.assertTrue(serverEvent.isSendAlarm(AlarmType.PRODUCER_SERVER_PIGEON_SERVICE, alarmMeta));
			} else if (i == 20) {
				Assert.assertTrue(serverEvent.isSendAlarm(AlarmType.PRODUCER_SERVER_PIGEON_SERVICE, alarmMeta));
			}else{
				Assert.assertFalse(serverEvent.isSendAlarm(AlarmType.PRODUCER_SERVER_PIGEON_SERVICE, alarmMeta));
			}
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}

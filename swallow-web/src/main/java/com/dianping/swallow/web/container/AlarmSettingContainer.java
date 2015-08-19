package com.dianping.swallow.web.container;

import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:49:38
 */
public interface AlarmSettingContainer {

	GlobalAlarmSetting findGlobalAlarmSetting(String globalId);

	ProducerServerAlarmSetting findProducerServerAlarmSetting(String serverId);

	ConsumerServerAlarmSetting findConsumerServerAlarmSetting(String serverId);

	TopicAlarmSetting findTopicAlarmSetting(String topicName);

	ConsumerIdAlarmSetting findConsumerIdAlarmSetting(String topicName, String consumerId);

}

package com.dianping.swallow.web.monitor.wapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 * @author mingdongli
 *
 *         2015年7月12日下午2:46:21
 */
@Component
public class TopicAlarmSettingServiceWrapper {

	@Resource(name = "consumerIdAlarmSettingService")
	private ConsumerIdAlarmSettingService consumerIdAlarmSettingService;

	@Resource(name = "topicAlarmSettingService")
	private TopicAlarmSettingService topicAlarmSettingService;

	public ConsumerBaseAlarmSetting loadConsumerBaseAlarmSetting(String consumerId) {

		ConsumerIdAlarmSetting consumerIdAlarmSetting = consumerIdAlarmSettingService.findDefault();
		if (consumerIdAlarmSetting == null) {
			return loadConsumerIdAlarmSettingWithMaxValue();
		}
		List<String> whiteList = topicAlarmSettingService.getConsumerIdWhiteList();
		if (whiteList.contains(consumerId)) {
			return loadConsumerIdAlarmSettingWithMaxValue();
		}

		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = consumerIdAlarmSetting.getConsumerAlarmSetting();
		if (consumerBaseAlarmSetting == null) {
			consumerBaseAlarmSetting = loadConsumerIdAlarmSettingWithMaxValue();
		}
		return consumerBaseAlarmSetting;
	}

	private ConsumerBaseAlarmSetting loadConsumerIdAlarmSettingWithMaxValue() {

		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = new ConsumerBaseAlarmSetting();

		consumerBaseAlarmSetting.setSendDelay(Integer.MAX_VALUE);
		consumerBaseAlarmSetting.setAckDelay(Integer.MAX_VALUE);
		consumerBaseAlarmSetting.setAccumulation(Integer.MAX_VALUE);
		return consumerBaseAlarmSetting;
	}

}

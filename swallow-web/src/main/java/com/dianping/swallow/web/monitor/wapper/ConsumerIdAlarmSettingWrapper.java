package com.dianping.swallow.web.monitor.wapper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;

/**
 * @author mingdongli
 *
 *         2015年7月23日下午7:22:10
 */
@Component
public class ConsumerIdAlarmSettingWrapper {

	@Resource(name = "consumerIdAlarmSettingService")
	ConsumerIdAlarmSettingService consumerIdAlarmSettingService;

	public ConsumerBaseAlarmSetting loadConsumerBaseAlarmSetting(String consumerId) throws Exception {

		List<ConsumerIdAlarmSetting> consumerBaseAlarmSettings = consumerIdAlarmSettingService
				.findByConsumerId(consumerId);
		
		if(consumerBaseAlarmSettings != null && consumerBaseAlarmSettings.size() > 0){
			ConsumerIdAlarmSetting consumerIdAlarmSetting = consumerBaseAlarmSettings.get(0);
			return consumerIdAlarmSetting.getConsumerAlarmSetting();
		}else{
			ConsumerIdAlarmSetting consumerIdAlarmSetting = consumerIdAlarmSettingService.findDefault();
			if(consumerIdAlarmSetting != null){
				return consumerIdAlarmSetting.getConsumerAlarmSetting();
			}
			throw new Exception("Default setting is not avalable.");
		}

	}

}

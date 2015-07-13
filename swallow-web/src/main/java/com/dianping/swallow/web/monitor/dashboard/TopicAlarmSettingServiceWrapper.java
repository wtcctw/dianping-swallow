package com.dianping.swallow.web.monitor.dashboard;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.service.TopicAlarmSettingService;


/**
 * @author mingdongli
 *
 * 2015年7月12日下午2:46:21
 */
@Component
public class TopicAlarmSettingServiceWrapper {
	
	@Resource(name = "topicAlarmSettingService")
	private TopicAlarmSettingService topicAlarmSettingService;
	
	public ConsumerBaseAlarmSetting loadConsumerBaseAlarmSetting(String consumerId){
		
		List<TopicAlarmSetting> topicAlarmSettings = topicAlarmSettingService.findAll();
		TopicAlarmSetting topicAlarmSetting;
		if(topicAlarmSettings.size() > 0){
			topicAlarmSetting = topicAlarmSettings.get(0);
		}
		else{
			return new ConsumerBaseAlarmSetting();
		}
		List<String> whiteList = topicAlarmSetting.getConsumerIdWhiteList();
		if(whiteList.contains(consumerId)){
			return loadConsumerBaseAlarmSettingWithMaxValue();
			
		}
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = topicAlarmSetting.getConsumerAlarmSetting();
		if(consumerBaseAlarmSetting == null){
			consumerBaseAlarmSetting = new ConsumerBaseAlarmSetting();
		}
		return consumerBaseAlarmSetting;
	}
	
	private ConsumerBaseAlarmSetting loadConsumerBaseAlarmSettingWithMaxValue(){
		
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = new ConsumerBaseAlarmSetting();
		
		consumerBaseAlarmSetting.setAccumulation(Integer.MAX_VALUE);
		consumerBaseAlarmSetting.setSenderDelay(Integer.MAX_VALUE);
		consumerBaseAlarmSetting.setAckDelay(Integer.MAX_VALUE);
		return consumerBaseAlarmSetting;
	}
	
}

package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.controller.dto.ConsumerServerAlarmSettingDto;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;


/**
 * @author mingdongli
 *
 * 2015年7月14日下午1:42:05
 */
public class ConsumerServerAlarmSettingMapper {
	
	private static final String DELIMITOR = ",";

	public static ConsumerServerAlarmSetting toConsumerServerAlarmSetting(ConsumerServerAlarmSettingDto dto) {
		

		ConsumerServerAlarmSetting alarmSetting = new ConsumerServerAlarmSetting();

		List<String> topicWhiteList = new ArrayList<String>();

		QPSAlarmSetting consumerSendQPSAlarmSetting = new QPSAlarmSetting();
		consumerSendQPSAlarmSetting.setPeak(dto.getConsumersendpeak());
		consumerSendQPSAlarmSetting.setValley(dto.getConsumersendvalley());
		consumerSendQPSAlarmSetting.setFluctuation(dto.getConsumersendfluctuation());
		alarmSetting.setSenderAlarmSetting(consumerSendQPSAlarmSetting);

		QPSAlarmSetting consumerAckQPSAlarmSetting = new QPSAlarmSetting();
		consumerAckQPSAlarmSetting.setPeak(dto.getConsumerackpeak());
		consumerAckQPSAlarmSetting.setValley(dto.getConsumerackvalley());
		consumerAckQPSAlarmSetting.setFluctuation(dto.getConsumerackfluctuation());
		alarmSetting.setAckAlarmSetting(consumerAckQPSAlarmSetting);
		
		alarmSetting.setServerId(dto.getServerId());
		
		String whiteList = dto.getWhitelist();
		String[] whiteLists = whiteList.split(DELIMITOR);
		for(String wl : whiteLists){
			if(!topicWhiteList.contains(wl)){
				topicWhiteList.add(wl);
			}
		}
		alarmSetting.setTopicWhiteList(topicWhiteList);
		
		return alarmSetting;
	}

	public static ConsumerServerAlarmSettingDto toConsumerServerAlarmSettingDto(ConsumerServerAlarmSetting alarmSetting) {

		ConsumerServerAlarmSettingDto dto = new ConsumerServerAlarmSettingDto();
		
		QPSAlarmSetting sendQPSAlarmSetting = alarmSetting.getSenderAlarmSetting();
		dto.setConsumersendpeak(sendQPSAlarmSetting.getPeak());
		dto.setConsumersendvalley(sendQPSAlarmSetting.getValley());
		dto.setConsumersendfluctuation(sendQPSAlarmSetting.getFluctuation());
		
		QPSAlarmSetting ackQPSAlarmSetting = alarmSetting.getAckAlarmSetting();
		dto.setConsumerackpeak(ackQPSAlarmSetting.getPeak());
		dto.setConsumerackvalley(ackQPSAlarmSetting.getValley());
		dto.setConsumerackfluctuation(ackQPSAlarmSetting.getFluctuation());
		
		
		List<String> whiteList = alarmSetting.getTopicWhiteList();
		dto.setWhitelist(StringUtils.join(whiteList, DELIMITOR));
		dto.setServerId(alarmSetting.getServerId());

		return dto;
	}

}

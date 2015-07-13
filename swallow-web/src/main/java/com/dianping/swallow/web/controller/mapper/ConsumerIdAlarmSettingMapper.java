package com.dianping.swallow.web.controller.mapper;

import com.dianping.swallow.web.controller.dto.ConsumerIdAlarmSettingDto;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;


/**
 * @author mingdongli
 *
 * 2015年7月13日下午2:56:41
 */
public class ConsumerIdAlarmSettingMapper {
	
	public static ConsumerIdAlarmSetting toConsumerIdAlarmSetting(ConsumerIdAlarmSettingDto dto) {
		
		ConsumerIdAlarmSetting alarmSetting = new ConsumerIdAlarmSetting();
		
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = new ConsumerBaseAlarmSetting();
		
		QPSAlarmSetting sendQPSAlarmSetting = new QPSAlarmSetting();
		
		sendQPSAlarmSetting.setPeak(dto.getSendpeak());
		sendQPSAlarmSetting.setValley(dto.getSendvalley());
		sendQPSAlarmSetting.setFluctuation(dto.getSendfluctuation());
		consumerBaseAlarmSetting.setSenderQpsAlarmSetting(sendQPSAlarmSetting);
		
		QPSAlarmSetting ackQPSAlarmSetting = new QPSAlarmSetting();
		
		ackQPSAlarmSetting.setPeak(dto.getAckpeak());
		ackQPSAlarmSetting.setValley(dto.getAckvalley());
		ackQPSAlarmSetting.setFluctuation(dto.getAckfluctuation());
		consumerBaseAlarmSetting.setAckQpsAlarmSetting(ackQPSAlarmSetting);
		
		consumerBaseAlarmSetting.setSenderDelay(dto.getSenddelay());
		consumerBaseAlarmSetting.setAckDelay(dto.getAckdelay());
		consumerBaseAlarmSetting.setAccumulation(dto.getAccumulation());
		
		alarmSetting.setConsumerAlarmSetting(consumerBaseAlarmSetting);
		return alarmSetting;
	}

	public static ConsumerIdAlarmSettingDto toConsumerIdAlarmSettingDto(ConsumerIdAlarmSetting alarmSetting) {
		
		ConsumerIdAlarmSettingDto dto = new ConsumerIdAlarmSettingDto();
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = alarmSetting.getConsumerAlarmSetting();
		
		QPSAlarmSetting sendQPSAlarmSetting = consumerBaseAlarmSetting.getSenderQpsAlarmSetting();
		dto.setSendpeak(sendQPSAlarmSetting.getPeak());
		dto.setSendvalley(sendQPSAlarmSetting.getValley());
		dto.setSendfluctuation(sendQPSAlarmSetting.getFluctuation());
		
		QPSAlarmSetting ackQPSAlarmSetting = consumerBaseAlarmSetting.getAckQpsAlarmSetting();
		dto.setAckpeak(ackQPSAlarmSetting.getPeak());
		dto.setAckvalley(ackQPSAlarmSetting.getValley());
		dto.setAckfluctuation(ackQPSAlarmSetting.getFluctuation());
		
		dto.setSenddelay(consumerBaseAlarmSetting.getSenderDelay());
		dto.setAckdelay(consumerBaseAlarmSetting.getAckDelay());
		dto.setAccumulation(consumerBaseAlarmSetting.getAccumulation());
		return dto;
	}

}

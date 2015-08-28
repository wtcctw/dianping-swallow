package com.dianping.swallow.web.controller.mapper;

import com.dianping.swallow.web.controller.dto.ConsumerServerResourceDto;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;

/**
 * @author mingdongli
 *
 *         2015年7月14日下午1:42:05
 */
public class ConsumerServerResourceMapper {

	public static ConsumerServerResource toConsumerResourceSetting(ConsumerServerResourceDto dto) {

		ConsumerServerResource consumerServerResource = new ConsumerServerResource();

		QPSAlarmSetting sendQPSAlarmSetting = new QPSAlarmSetting();
		sendQPSAlarmSetting.setPeak(dto.getSendpeak());
		sendQPSAlarmSetting.setValley(dto.getSendvalley());
		sendQPSAlarmSetting.setFluctuation(dto.getSendfluctuation());
		sendQPSAlarmSetting.setFluctuationBase(dto.getSendfluctuationBase());
		consumerServerResource.setSendAlarmSetting(sendQPSAlarmSetting);

		QPSAlarmSetting ackQPSAlarmSetting = new QPSAlarmSetting();
		ackQPSAlarmSetting.setPeak(dto.getAckpeak());
		ackQPSAlarmSetting.setValley(dto.getAckvalley());
		ackQPSAlarmSetting.setFluctuation(dto.getAckfluctuation());
		ackQPSAlarmSetting.setFluctuationBase(dto.getAckfluctuationBase());
		consumerServerResource.setAckAlarmSetting(ackQPSAlarmSetting);

		consumerServerResource.setId(dto.getId());
		consumerServerResource.setIp(dto.getIp());
		consumerServerResource.setAlarm(dto.isAlarm());
		consumerServerResource.setHostname(dto.getHostname());

		return consumerServerResource;
	}

	public static ConsumerServerResourceDto toConsumerServerResourceDto(ConsumerServerResource consumerServerResourceDto) {

		ConsumerServerResourceDto dto = new ConsumerServerResourceDto();

		QPSAlarmSetting sendQPSAlarmSetting = consumerServerResourceDto.getSendAlarmSetting();
		dto.setSendpeak(sendQPSAlarmSetting.getPeak());
		dto.setSendvalley(sendQPSAlarmSetting.getValley());
		dto.setSendfluctuation(sendQPSAlarmSetting.getFluctuation());
		dto.setSendfluctuationBase(sendQPSAlarmSetting.getFluctuationBase());

		QPSAlarmSetting ackQPSAlarmSetting = consumerServerResourceDto.getAckAlarmSetting();
		dto.setAckpeak(ackQPSAlarmSetting.getPeak());
		dto.setAckvalley(ackQPSAlarmSetting.getValley());
		dto.setAckfluctuation(ackQPSAlarmSetting.getFluctuation());
		dto.setAckfluctuationBase(ackQPSAlarmSetting.getFluctuationBase());
		
		dto.setId(consumerServerResourceDto.getId());
		dto.setIp(consumerServerResourceDto.getIp());
		dto.setHostname(consumerServerResourceDto.getHostname());
		dto.setAlarm(consumerServerResourceDto.isAlarm());

		return dto;
	}

}

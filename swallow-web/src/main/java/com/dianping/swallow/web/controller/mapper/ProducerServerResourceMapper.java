package com.dianping.swallow.web.controller.mapper;

import com.dianping.swallow.web.controller.dto.ServerResourceDto;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ProducerServerResource;


public class ProducerServerResourceMapper {

	public static ProducerServerResource toProducerServerResource(ServerResourceDto dto) {

		ProducerServerResource producerServerResource = new ProducerServerResource();

		QPSAlarmSetting producerQPSAlarmSetting = new QPSAlarmSetting();
		
		producerQPSAlarmSetting.setPeak(dto.getSendpeak());
		producerQPSAlarmSetting.setValley(dto.getSendvalley());
		producerQPSAlarmSetting.setFluctuation(dto.getSendfluctuation());
		producerQPSAlarmSetting.setFluctuationBase(dto.getSendfluctuationBase());
		
		producerServerResource.setSendAlarmSetting(producerQPSAlarmSetting);

		producerServerResource.setId(dto.getId());
		producerServerResource.setIp(dto.getIp());
		producerServerResource.setHostname(dto.getHostname());
		producerServerResource.setAlarm(dto.isAlarm());

		return producerServerResource;
	}

	public static ServerResourceDto toProducerServerResourceDto(ProducerServerResource producerServerResource) {

		ServerResourceDto dto = new ServerResourceDto();

		QPSAlarmSetting producerQPSAlarmSetting = producerServerResource.getSendAlarmSetting();
		dto.setSendpeak(producerQPSAlarmSetting.getPeak());
		dto.setSendvalley(producerQPSAlarmSetting.getValley());
		dto.setSendfluctuation(producerQPSAlarmSetting.getFluctuation());
		dto.setSendfluctuationBase(producerQPSAlarmSetting.getFluctuationBase());
		
		dto.setId(producerServerResource.getId());
		dto.setIp(producerServerResource.getIp());
		dto.setHostname(producerServerResource.getHostname());
		dto.setAlarm(producerServerResource.isAlarm());

		return dto;
	}
}

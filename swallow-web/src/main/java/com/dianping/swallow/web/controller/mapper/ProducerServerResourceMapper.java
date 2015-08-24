package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.controller.dto.ServerResourceDto;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ProducerServerResource;


public class ProducerServerResourceMapper {

	private static final String DELIMITOR = ",";

	public static ProducerServerResource toProducerServerResource(ServerResourceDto dto) {

		ProducerServerResource producerServerResource = new ProducerServerResource();

		List<String> topicWhiteList = new ArrayList<String>();

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

		String whiteList = dto.getWhitelist();
		if (StringUtils.isNotBlank(whiteList)) {
			String[] whiteLists = whiteList.split(DELIMITOR);
			for (String wl : whiteLists) {
				if (!topicWhiteList.contains(wl)) {
					topicWhiteList.add(wl);
				}
			}
		}
		producerServerResource.setTopicWhiteList(topicWhiteList);

		return producerServerResource;
	}

	public static ServerResourceDto toProducerServerResourceDto(ProducerServerResource producerServerResource) {

		ServerResourceDto dto = new ServerResourceDto();

		QPSAlarmSetting producerQPSAlarmSetting = producerServerResource.getSendAlarmSetting();
		dto.setSendpeak(producerQPSAlarmSetting.getPeak());
		dto.setSendvalley(producerQPSAlarmSetting.getValley());
		dto.setSendfluctuation(producerQPSAlarmSetting.getFluctuation());
		dto.setSendfluctuationBase(producerQPSAlarmSetting.getFluctuationBase());

		List<String> whiteList = producerServerResource.getTopicWhiteList();
		dto.setWhitelist(StringUtils.join(whiteList, DELIMITOR));
		
		dto.setId(producerServerResource.getId());
		dto.setIp(producerServerResource.getIp());
		dto.setHostname(producerServerResource.getHostname());
		dto.setAlarm(producerServerResource.isAlarm());

		return dto;
	}
}

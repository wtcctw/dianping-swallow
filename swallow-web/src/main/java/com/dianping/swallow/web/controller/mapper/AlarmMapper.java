package com.dianping.swallow.web.controller.mapper;

import com.dianping.swallow.web.controller.dto.AlarmDto;
import com.dianping.swallow.web.model.alarm.Alarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmMapper {

	public static AlarmDto convertToAlarmDto(Alarm alarm) {
		AlarmDto alarmDto = new AlarmDto();
		alarmDto.setEventId(alarm.getEventId());
		alarmDto.setNumber(alarm.getNumber());
		alarmDto.setType(alarm.getType());
		alarmDto.setTitle(alarm.getTitle());
		alarmDto.setBody(alarm.getBody());
		if (alarm.getRelatedType().isCConsumerId()) {
			alarmDto.setRelated(alarm.getSubRelated());
		} else {
			alarmDto.setRelated(alarm.getRelated());
		}
		if (alarm.getRelatedType().isPServerIp() || alarm.getRelatedType().isPIp()
				|| alarm.getRelatedType().isCServerIp() || alarm.getRelatedType().isCIp() || alarm.getRelatedType().isKServerIp()) {
			alarmDto.setRelatedUrl("/console/ip?ip=" + alarm.getRelated());
		} else if (alarm.getRelatedType().isPTopic() || alarm.getRelatedType().isCTopic()) {
			alarmDto.setRelatedUrl("/console/ip?topic=" + alarm.getRelated());
		} else if (alarm.getRelatedType().isCConsumerId()) {
			alarmDto.setRelatedUrl("/console/ip?topic=" + alarm.getRelated() + "&cid=" + alarm.getSubRelated());
		}
		alarmDto.setRelatedType(alarm.getRelatedType());
		alarmDto.setSendInfos(alarm.getSendInfos());
		alarmDto.setSourceIp(alarm.getSourceIp());
		alarmDto.setCreateTime(alarm.getCreateTime());
		return alarmDto;
	}

	public static List<AlarmDto> getAlarmDtos(List<Alarm> alarms) {
		if (alarms != null) {
			List<AlarmDto> alarmDtos = new ArrayList<AlarmDto>();
			for (Alarm alarm : alarms) {
				alarmDtos.add(convertToAlarmDto(alarm));
			}
			return alarmDtos;
		}
		return null;
	}
}

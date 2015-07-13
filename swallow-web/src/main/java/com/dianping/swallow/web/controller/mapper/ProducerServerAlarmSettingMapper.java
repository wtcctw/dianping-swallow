package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.controller.dto.ProducerServerAlarmSettingDto;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;

public class ProducerServerAlarmSettingMapper {

	private static final String WHITELIST_SPLIT = ",";

	public static ProducerServerAlarmSetting toProducerServerAlarmSetting(ProducerServerAlarmSettingDto dto) {
		ProducerServerAlarmSetting alarmSetting = new ProducerServerAlarmSetting();
		List<String> whiteList = null;
		if (dto.getWhiteList() != null) {
			whiteList = new ArrayList<String>();
			String strWhiteArr[] = dto.getWhiteList().split(WHITELIST_SPLIT);
			for (String whiteName : strWhiteArr) {
				if (StringUtils.isNotBlank(whiteName)) {
					whiteList.add(whiteName);
				}
			}
		}
		alarmSetting.setWhiteList(whiteList);
		alarmSetting.setCreateTime(dto.getCreateTime());
		alarmSetting.setUpdateTime(dto.getCreateTime());
		QPSAlarmSetting qps = new QPSAlarmSetting();
		qps.setPeak(dto.getPeak());
		qps.setValley(dto.getValley());
		qps.setFluctuation(dto.getFluctuation());
		alarmSetting.setDefaultAlarmSetting(qps);
		return alarmSetting;
	}

	public static ProducerServerAlarmSettingDto toProducerServerAlarmSettingDto(ProducerServerAlarmSetting alarmSetting) {
		ProducerServerAlarmSettingDto dto = new ProducerServerAlarmSettingDto();
		StringBuilder strWhiteBuilder = new StringBuilder();
		if (alarmSetting.getWhiteList() != null) {
			for (String whiteName : alarmSetting.getWhiteList()) {
				if (StringUtils.isNotBlank(whiteName)) {
					strWhiteBuilder.append(whiteName).append(WHITELIST_SPLIT);
				}
			}
		}
		String strWhite = strWhiteBuilder.toString();
		if (strWhite.length() > 0) {
			strWhite = strWhite.substring(0, strWhite.length());
		}
		dto.setWhiteList(strWhite);
		dto.setCreateTime(alarmSetting.getCreateTime());
		dto.setUpdateTime(alarmSetting.getCreateTime());
		if (alarmSetting.getDefaultAlarmSetting() != null) {
			dto.setPeak(alarmSetting.getDefaultAlarmSetting().getPeak());
			dto.setValley(alarmSetting.getDefaultAlarmSetting().getValley());
			dto.setFluctuation(alarmSetting.getDefaultAlarmSetting().getFluctuation());
		}
		return dto;
	}

}

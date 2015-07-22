package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.controller.dto.GlobalAlarmSettingDto;
import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;

public class GlobalAlarmSettingMapper {

	private static final String DELIMITOR = ",";

	public static GlobalAlarmSetting toSwallowAlarmSetting(GlobalAlarmSettingDto dto) {
		if (dto == null) {
			return null;
		}
		GlobalAlarmSetting globalAlarmSetting = new GlobalAlarmSetting();
		globalAlarmSetting.setSwallowId(dto.getSwallowId());
		String strProducerWhite = dto.getProducerWhiteList();
		globalAlarmSetting.setProducerWhiteList(convertToList(strProducerWhite));
		String strConsumerWhite = dto.getConsumerWhiteList();
		globalAlarmSetting.setConsumerWhiteList(convertToList(strConsumerWhite));
		return globalAlarmSetting;
	}

	public static GlobalAlarmSettingDto toSwallowAlarmSettingDto(GlobalAlarmSetting globalAlarmSetting) {
		if (globalAlarmSetting == null) {
			return null;
		}
		GlobalAlarmSettingDto dto = new GlobalAlarmSettingDto();
		dto.setSwallowId(globalAlarmSetting.getSwallowId());
		dto.setProducerWhiteList(convertToStr(globalAlarmSetting.getProducerWhiteList()));
		dto.setConsumerWhiteList(convertToStr(globalAlarmSetting.getConsumerWhiteList()));
		return dto;
	}

	private static List<String> convertToList(String strValue) {
		if (strValue != null) {
			List<String> lists = new ArrayList<String>();
			String[] whites = strValue.split(DELIMITOR);
			for (String white : whites) {
				if (StringUtils.isNotBlank(white)) {
					lists.add(white);
				}
			}
			return lists;
		}
		return null;
	}

	private static String convertToStr(List<String> lists) {
		StringBuilder strBuilder = new StringBuilder();
		if (lists == null || lists.size() == 0) {
			return "";
		} else {
			for (String strValue : lists) {
				strBuilder.append(strValue + DELIMITOR);
			}
		}
		String result = strBuilder.toString();
		if (StringUtils.isNotBlank(result)) {
			return result.substring(0, result.length() - 1);
		}
		return "";
	}

}

package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.controller.dto.SwallowAlarmSettingDto;
import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;

public class SwallowAlarmSettingMapper {

	private static final String DELIMITOR = ",";

	public static GlobalAlarmSetting toSwallowAlarmSetting(SwallowAlarmSettingDto dto) {
		if (dto == null) {
			return null;
		}
		GlobalAlarmSetting swallowAlarmSetting = new GlobalAlarmSetting();
		swallowAlarmSetting.setSwallowId(dto.getSwallowId());
		String strProducerWhite = dto.getProducerWhiteList();
		swallowAlarmSetting.setProducerWhiteList(convertToList(strProducerWhite));
		String strConsumerWhite = dto.getConsumerWhiteList();
		swallowAlarmSetting.setConsumerWhiteList(convertToList(strConsumerWhite));
		return swallowAlarmSetting;
	}

	public static SwallowAlarmSettingDto toSwallowAlarmSettingDto(GlobalAlarmSetting swallowAlarmSetting) {
		if (swallowAlarmSetting == null) {
			return null;
		}
		SwallowAlarmSettingDto dto = new SwallowAlarmSettingDto();
		dto.setSwallowId(swallowAlarmSetting.getSwallowId());
		dto.setProducerWhiteList(convertToStr(swallowAlarmSetting.getProducerWhiteList()));
		dto.setConsumerWhiteList(convertToStr(swallowAlarmSetting.getConsumerWhiteList()));
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

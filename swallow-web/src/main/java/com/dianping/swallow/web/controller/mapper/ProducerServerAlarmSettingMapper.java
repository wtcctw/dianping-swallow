package com.dianping.swallow.web.controller.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.controller.dto.ProducerServerAlarmSettingDto;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerServerAlarmSettingMapper {

	private static final String DELIMITOR = ",";

	public static ProducerServerAlarmSetting toProducerServerAlarmSetting(ProducerServerAlarmSettingDto dto) {

		ProducerServerAlarmSetting alarmSetting = new ProducerServerAlarmSetting();

		List<String> topicWhiteList = new ArrayList<String>();

		QPSAlarmSetting producerQPSAlarmSetting = new QPSAlarmSetting();
		producerQPSAlarmSetting.setPeak(dto.getProducerpeak());
		producerQPSAlarmSetting.setValley(dto.getProducervalley());
		producerQPSAlarmSetting.setFluctuation(dto.getProducerfluctuation());
		alarmSetting.setDefaultAlarmSetting(producerQPSAlarmSetting);

		alarmSetting.setServerId(dto.getServerId());

		String whiteList = dto.getWhitelist();
		if (StringUtils.isNotBlank(whiteList)) {
			String[] whiteLists = whiteList.split(DELIMITOR);
			for (String wl : whiteLists) {
				if (!topicWhiteList.contains(wl)) {
					topicWhiteList.add(wl);
				}
			}
		}
		alarmSetting.setTopicWhiteList(topicWhiteList);

		return alarmSetting;
	}

	public static ProducerServerAlarmSettingDto toProducerServerAlarmSettingDto(ProducerServerAlarmSetting alarmSetting) {

		ProducerServerAlarmSettingDto dto = new ProducerServerAlarmSettingDto();

		QPSAlarmSetting producerQPSAlarmSetting = alarmSetting.getDefaultAlarmSetting();
		dto.setProducerpeak(producerQPSAlarmSetting.getPeak());
		dto.setProducervalley(producerQPSAlarmSetting.getValley());
		dto.setProducerfluctuation(producerQPSAlarmSetting.getFluctuation());

		List<String> whiteList = alarmSetting.getTopicWhiteList();
		dto.setWhitelist(StringUtils.join(whiteList, DELIMITOR));
		dto.setServerId(alarmSetting.getServerId());

		return dto;
	}
}

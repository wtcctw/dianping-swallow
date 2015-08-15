package com.dianping.swallow.web.controller.mapper;


import com.dianping.swallow.web.controller.dto.AlarmMetaDto;
import com.dianping.swallow.web.model.alarm.AlarmMeta;

/**
 * 
 * @author qiyin
 *
 *         2015年8月11日 下午5:41:26
 */
public class AlarmMetaMapper {

	public static AlarmMeta convertToAlarmMeta(AlarmMetaDto alarmMetaDto) {
		AlarmMeta alarmMeta = new AlarmMeta();
		alarmMeta.setMetaId(alarmMetaDto.getMetaId());
		alarmMeta.setType(alarmMetaDto.getType());
		alarmMeta.setLevelType(alarmMetaDto.getLevelType());
		alarmMeta.setIsSmsMode(alarmMetaDto.getIsSmsMode());
		alarmMeta.setIsWeiXinMode(alarmMetaDto.getIsWeiXinMode());
		alarmMeta.setIsMailMode(alarmMetaDto.getIsMailMode());
		alarmMeta.setIsSendSwallow(alarmMetaDto.getIsSendSwallow());
		alarmMeta.setIsSendBusiness(alarmMetaDto.getIsSendBusiness());
		alarmMeta.setAlarmTitle(alarmMetaDto.getAlarmTitle());
		alarmMeta.setAlarmTemplate(alarmMetaDto.getAlarmTemplate());
		alarmMeta.setAlarmDetail(alarmMetaDto.getAlarmDetail());
		alarmMeta.setMaxTimeSpan(alarmMetaDto.getMaxTimeSpan());
		alarmMeta.setDaySpanBase(alarmMetaDto.getDaySpanBase());
		alarmMeta.setNightSpanBase(alarmMetaDto.getNightSpanBase());
		alarmMeta.setCreateTime(alarmMetaDto.getCreateTime());
		alarmMeta.setUpdateTime(alarmMetaDto.getUpdateTime());
		return alarmMeta;
	}

}

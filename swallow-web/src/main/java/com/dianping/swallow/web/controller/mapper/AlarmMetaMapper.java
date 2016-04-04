package com.dianping.swallow.web.controller.mapper;


import com.dianping.swallow.web.controller.dto.AlarmMetaBatchDto.UpdateType;
import com.dianping.swallow.web.controller.dto.AlarmMetaDto;
import com.dianping.swallow.web.model.alarm.AlarmMeta;

/**
 * @author qiyin
 *         <p/>
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
        alarmMeta.setMajorTopics(alarmMetaDto.getMajorTopics());
        alarmMeta.setMaxTimeSpan(alarmMetaDto.getMaxTimeSpan());
        alarmMeta.setDaySpanBase(alarmMetaDto.getDaySpanBase());
        alarmMeta.setNightSpanBase(alarmMetaDto.getNightSpanBase());
        alarmMeta.setCreateTime(alarmMetaDto.getCreateTime());
        alarmMeta.setUpdateTime(alarmMetaDto.getUpdateTime());
        return alarmMeta;
    }

    public static void update(UpdateType updateType, AlarmMeta alarmMeta, boolean isOpen) {
        switch (updateType) {
            case SMS:
                alarmMeta.setIsSmsMode(isOpen);
                break;
            case WEIXIN:
                alarmMeta.setIsWeiXinMode(isOpen);
                break;
            case MAIL:
                alarmMeta.setIsMailMode(isOpen);
                break;
            case SWALLOW:
                alarmMeta.setIsSendSwallow(isOpen);
                break;
            case BUSINESS:
                alarmMeta.setIsSendBusiness(isOpen);
                break;
        }
    }

}

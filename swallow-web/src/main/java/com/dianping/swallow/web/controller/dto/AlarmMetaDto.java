package com.dianping.swallow.web.controller.dto;

import java.util.Date;
import java.util.List;

import com.dianping.swallow.web.model.alarm.AlarmLevelType;
import com.dianping.swallow.web.model.alarm.AlarmType;

/**
 * @author qiyin
 *         <p/>
 *         2015年8月11日 下午5:38:51
 */
public class AlarmMetaDto {

    private int metaId;

    private AlarmType type;

    private AlarmLevelType levelType;

    private boolean isSmsMode;

    private boolean isWeiXinMode;

    private boolean isMailMode;

    private boolean isSendSwallow;

    private boolean isSendBusiness;

    private String alarmTitle;

    private String alarmTemplate;

    private String alarmDetail;

    private List<String> majorTopics;

    private int maxTimeSpan;

    private int daySpanBase;

    private int nightSpanBase;

    private Date createTime;

    private Date updateTime;

    private boolean isUpdate;

    public int getMetaId() {
        return metaId;
    }

    public void setMetaId(int metaId) {
        this.metaId = metaId;
    }

    public AlarmType getType() {
        return type;
    }

    public void setType(AlarmType type) {
        this.type = type;
    }

    public AlarmLevelType getLevelType() {
        return levelType;
    }

    public boolean getIsSmsMode() {
        return isSmsMode;
    }

    public boolean getIsWeiXinMode() {
        return isWeiXinMode;
    }

    public boolean getIsMailMode() {
        return isMailMode;
    }

    public boolean getIsSendSwallow() {
        return isSendSwallow;
    }

    public boolean getIsSendBusiness() {
        return isSendBusiness;
    }

    public String getAlarmTitle() {
        return alarmTitle;
    }

    public String getAlarmTemplate() {
        return alarmTemplate;
    }

    public String getAlarmDetail() {
        return alarmDetail;
    }

    public void setAlarmDetail(String alarmDetail) {
        this.alarmDetail = alarmDetail;
    }

    public List<String> getMajorTopics() {
        return majorTopics;
    }

    public void setMajorTopics(List<String> majorTopics) {
        this.majorTopics = majorTopics;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getMaxTimeSpan() {
        return maxTimeSpan;
    }

    public boolean getIsUpdate() {
        return isUpdate;
    }

    public int getDaySpanBase() {
        return daySpanBase;
    }

    public int getNightSpanBase() {
        return nightSpanBase;
    }

}

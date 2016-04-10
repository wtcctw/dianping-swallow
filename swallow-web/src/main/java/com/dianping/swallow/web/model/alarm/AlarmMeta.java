package com.dianping.swallow.web.model.alarm;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author qiyin
 *         <p/>
 *         2015年10月16日 上午10:08:29
 */
public class AlarmMeta {

    @Transient
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Transient
    public static final String IP_TEMPLATE = "{ip}";

    @Transient
    public static final String DATE_TEMPLATE = "{date}";

    @Transient
    public static final String CURRENTVALUE_TEMPLATE = "{currentValue}";

    @Transient
    public static final String EXPECTEDVALUE_TEMPLATE = "{expectedValue}";

    @Transient
    public static final String MASTERIP_TEMPLATE = "{masterIp}";

    @Transient
    public static final String SLAVEIP_TEMPLATE = "{slaveIp}";

    @Transient
    public static final String TOPIC_TEMPLATE = "{topic}";

    @Transient
    public static final String CONSUMERID_TEMPLATE = "{consumerId}";

    @Transient
    public static final String CHECKINTERVAL_TEMPLATE = "{checkInterval}";

    private String id;

    @Indexed(name = "IX_METAID", direction = IndexDirection.DESCENDING)
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public void setLevelType(AlarmLevelType levelType) {
        this.levelType = levelType;
    }

    public boolean getIsSmsMode() {
        return isSmsMode;
    }

    public void setIsSmsMode(boolean isSmsMode) {
        this.isSmsMode = isSmsMode;
    }

    public boolean getIsWeiXinMode() {
        return isWeiXinMode;
    }

    public void setIsWeiXinMode(boolean isWeiXinMode) {
        this.isWeiXinMode = isWeiXinMode;
    }

    public boolean getIsMailMode() {
        return isMailMode;
    }

    public void setIsMailMode(boolean isMailMode) {
        this.isMailMode = isMailMode;
    }

    public boolean getIsSendSwallow() {
        return isSendSwallow;
    }

    public void setIsSendSwallow(boolean isSendSwallow) {
        this.isSendSwallow = isSendSwallow;
    }

    public boolean getIsSendBusiness() {
        return isSendBusiness;
    }

    public void setIsSendBusiness(boolean isSendBusiness) {
        this.isSendBusiness = isSendBusiness;
    }

    public String getAlarmTitle() {
        return alarmTitle;
    }

    public void setAlarmTitle(String alarmTitle) {
        this.alarmTitle = alarmTitle;
    }

    public String getAlarmTemplate() {
        return alarmTemplate;
    }

    public void setAlarmTemplate(String alarmTemplate) {
        this.alarmTemplate = alarmTemplate;
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

    public void setMaxTimeSpan(int maxTimeSpan) {
        this.maxTimeSpan = maxTimeSpan;
    }

    public int getDaySpanBase() {
        return daySpanBase;
    }

    public void setDaySpanBase(int daySpanBase) {
        this.daySpanBase = daySpanBase;
    }

    public int getNightSpanBase() {
        return nightSpanBase;
    }

    public void setNightSpanBase(int nightSpanBase) {
        this.nightSpanBase = nightSpanBase;
    }

    @Override
    public String toString() {
        return "AlarmMeta [id=" + id + ", metaId=" + metaId + ", type=" + type + ", levelType=" + levelType
                + ", isSmsMode=" + isSmsMode + ", isWeiXinMode=" + isWeiXinMode + ", isMailMode=" + isMailMode
                + ", isSendSwallow=" + isSendSwallow + ", isSendBusiness=" + isSendBusiness + ", alarmTitle="
                + alarmTitle + ", alarmTemplate=" + alarmTemplate + ", alarmDetail=" + alarmDetail + ", maxTimeSpan="
                + maxTimeSpan + ", daySpanBase=" + daySpanBase + ", nightSpanBase=" + nightSpanBase + ", createTime="
                + createTime + ", updateTime=" + updateTime + "]";
    }

}

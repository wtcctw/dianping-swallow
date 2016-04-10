package com.dianping.swallow.web.model.alarm;

/**
 * @author qiyin
 *         <p/>
 *         2015年8月5日 上午10:46:40
 */
public class ProducerBaseAlarmSetting {

    private QPSAlarmSetting qpsAlarmSetting;

    private boolean isQpsAlarm;

    private boolean isDelayAlarm;

    private long delay;

    private boolean isIpAlarm;

    public QPSAlarmSetting getQpsAlarmSetting() {
        return qpsAlarmSetting;
    }

    public void setQpsAlarmSetting(QPSAlarmSetting qpsAlarmSetting) {
        this.qpsAlarmSetting = qpsAlarmSetting;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isIpAlarm() {
        return isIpAlarm;
    }

    public void setIsIpAlarm(boolean isIpAlarm) {
        this.isIpAlarm = isIpAlarm;
    }

    public boolean isDelayAlarm() {
        return isDelayAlarm;
    }

    public void setIsDelayAlarm(boolean isDelayAlarm) {
        this.isDelayAlarm = isDelayAlarm;
    }

    @Override
    public String toString() {
        return "ProducerClientBaseAlarmSetting [ qpsAlarmSetting = " + qpsAlarmSetting + ", delay = " + delay + ", isDelayAlarm = " + isDelayAlarm + ", isIpAlarm = " + isIpAlarm + ", isQpsAlarm=" + isQpsAlarm + "]";
    }

    public boolean isQpsAlarm() {
        return isQpsAlarm;
    }

    public void setIsQpsAlarm(boolean isQpsAlarm) {
        this.isQpsAlarm = isQpsAlarm;
    }

}

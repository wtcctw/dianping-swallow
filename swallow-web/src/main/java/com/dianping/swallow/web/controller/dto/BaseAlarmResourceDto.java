package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *         <p/>
 *         2015年10月15日下午5:10:23
 */
public class BaseAlarmResourceDto extends BaseResourceDto {

    private boolean alarm;

    private long sendpeak;

    private long sendvalley;

    private int sendfluctuation;

    private long sendfluctuationBase;

    private long senddelay;

    public boolean isAlarm() {
        return alarm;
    }

    public long getSendpeak() {
        return sendpeak;
    }

    public long getSendvalley() {
        return sendvalley;
    }

    public int getSendfluctuation() {
        return sendfluctuation;
    }

    public long getSendfluctuationBase() {
        return sendfluctuationBase;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public void setSendpeak(long sendpeak) {
        this.sendpeak = sendpeak;
    }

    public void setSendvalley(long sendvalley) {
        this.sendvalley = sendvalley;
    }

    public void setSendfluctuation(int sendfluctuation) {
        this.sendfluctuation = sendfluctuation;
    }

    public void setSendfluctuationBase(long sendfluctuationBase) {
        this.sendfluctuationBase = sendfluctuationBase;
    }

    public long getSenddelay() {
        return senddelay;
    }

    public void setSenddelay(long senddelay) {
        this.senddelay = senddelay;
    }
}

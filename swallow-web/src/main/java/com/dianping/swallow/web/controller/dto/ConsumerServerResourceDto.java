package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年7月14日下午1:40:28
 */
public class ConsumerServerResourceDto  extends ServerResourceDto{
	
	private long ackpeak;
	
	private long ackvalley;
	
	private int ackfluctuation;
	
	private long ackfluctuationBase;

	public long getAckpeak() {
		return ackpeak;
	}

	public void setAckpeak(long ackpeak) {
		this.ackpeak = ackpeak;
	}

	public long getAckvalley() {
		return ackvalley;
	}

	public void setAckvalley(long ackvalley) {
		this.ackvalley = ackvalley;
	}

	public int getAckfluctuation() {
		return ackfluctuation;
	}

	public void setAckfluctuation(int ackfluctuation) {
		this.ackfluctuation = ackfluctuation;
	}

	public long getAckfluctuationBase() {
		return ackfluctuationBase;
	}

	public void setAckfluctuationBase(long ackFluctuationBase) {
		this.ackfluctuationBase = ackFluctuationBase;
	}

}

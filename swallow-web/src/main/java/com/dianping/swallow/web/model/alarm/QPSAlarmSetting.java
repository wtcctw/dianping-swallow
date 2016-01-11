package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 * 2015年8月5日 上午10:46:54
 */
public class QPSAlarmSetting {

	private long peak;

	private long valley;

	private int fluctuation;
	
	private long fluctuationBase;

	public long getPeak() {
		return peak;
	}

	public void setPeak(long peak) {
		this.peak = peak;
	}

	public long getValley() {
		return valley;
	}

	public void setValley(long valley) {
		this.valley = valley;
	}

	public int getFluctuation() {
		return fluctuation;
	}

	public void setFluctuation(int fluctuation) {
		this.fluctuation = fluctuation;
	}

	public long getFluctuationBase() {
		return fluctuationBase;
	}

	public void setFluctuationBase(long fluctuationBase) {
		this.fluctuationBase = fluctuationBase;
	}

}

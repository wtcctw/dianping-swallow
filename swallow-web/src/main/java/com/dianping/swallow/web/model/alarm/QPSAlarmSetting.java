package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class QPSAlarmSetting {

	private long peak;
	
	private long valley;
	
	private long fluctuation;

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

	public long getFluctuation() {
		return fluctuation;
	}

	public void setFluctuation(long fluctuation) {
		this.fluctuation = fluctuation;
	}

}

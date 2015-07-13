package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class QPSAlarmSetting {

	private long peak;

	private long valley;

	private int fluctuation;

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

	@Override
	public String toString() {
		return "QPSAlarmSetting[ peak = " + peak + ", valley=" + valley + ", fluctuation = " + fluctuation + "]";
	}

}

package com.dianping.swallow.web.model.event;

public class StatisEvent extends Event {

	private long currentValue;

	private long expectedValue;

	public long getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(long currentValue) {
		this.currentValue = currentValue;
	}

	public long getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(long expectedValue) {
		this.expectedValue = expectedValue;
	}
	
}

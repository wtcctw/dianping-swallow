package com.dianping.swallow.web.model.event;

public abstract class StatisEvent extends Event {

	private long currentValue;

	private long expectedValue;

	private StatisType statisType;

	public long getCurrentValue() {
		return currentValue;
	}

	public StatisEvent setCurrentValue(long currentValue) {
		this.currentValue = currentValue;
		return this;
	}

	public long getExpectedValue() {
		return expectedValue;
	}

	public StatisEvent setExpectedValue(long expectedValue) {
		this.expectedValue = expectedValue;
		return this;
	}

	public StatisType getStatisType() {
		return statisType;
	}

	public StatisEvent setStatisType(StatisType statisType) {
		this.statisType = statisType;
		return this;
	}

	@Override
	public String toString() {
		return "StatisEvent [currentValue=" + currentValue + ", expectedValue=" + expectedValue + ", statisType="
				+ statisType + "]";
	}

}

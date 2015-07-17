package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public enum AlarmLevelType {
	CRITICAL("严重告警"),
	MAJOR("重大告警"),
	MINOR("次要告警"),
	GENERAL("一般告警"),
	WARNING("警告告警");

	private String desc;
	
	private AlarmLevelType(){
		
	}
	
	private AlarmLevelType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return desc;
	}

	public boolean isCRITICAL() {
		return this == CRITICAL;
	}

	public boolean isMAJOR() {
		return this == MAJOR;
	}

	public boolean isMINOR() {
		return this == MINOR;
	}

	public boolean isGENERAL() {
		return this == GENERAL;
	}

	public boolean isWARNING() {
		return this == WARNING;
	}

}
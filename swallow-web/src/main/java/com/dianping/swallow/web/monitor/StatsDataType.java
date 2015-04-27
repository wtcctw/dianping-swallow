package com.dianping.swallow.web.monitor;

/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:05:42
 */
public enum StatsDataType {
	
	SAVE_DELAY("用户发送-存储延时"),
	SEND_DELAY("存储-发送延时"),
	ACK_DELAY("发送-ack延时"),
	
	SAVE_QPX("用户发送频率"),
	SEND_QPX("swallow发送频率"),
	ACK_QPX("用户返回ack频率");
	
	private String desc;
	StatsDataType(String desc){
		this.desc = desc;
	}

	@Override
	public String toString(){
		return desc;
	}
}

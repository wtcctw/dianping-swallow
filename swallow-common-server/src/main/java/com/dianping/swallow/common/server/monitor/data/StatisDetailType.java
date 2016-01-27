package com.dianping.swallow.common.server.monitor.data;

/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:05:42
 */
public enum StatisDetailType {
		
	SAVE_DELAY("用户发送-存储延时"),
	SEND_DELAY("存储-发送延时"),
	ACK_DELAY("发送-ack延时"),
	
	SAVE_QPX("用户发送频率"),
	SEND_QPX("swallow发送频率"),
	ACK_QPX("用户返回ack频率"),
	MSG_SEND("发送消息数"),

	ACCUMULATION("堆积消息量");
	
	private String 	desc;
	
	StatisDetailType(String desc){
		this.desc = desc;
	}

	@Override
	public String toString(){
		return desc;
	}
}

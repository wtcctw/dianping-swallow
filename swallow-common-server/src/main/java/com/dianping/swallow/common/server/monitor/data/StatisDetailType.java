package com.dianping.swallow.common.server.monitor.data;

/**
 * @author mengwenchao
 *
 * 2015年4月23日 上午11:05:42
 */
public enum StatisDetailType {
		
	SAVE_DELAY("用户发送-存储延时", 1),
	SEND_DELAY("存储-发送延时", 1),
	ACK_DELAY("发送-ack延时", 1),
	
	SAVE_QPX("用户发送频率", 2),
	SEND_QPX("swallow发送频率", 2),
	ACK_QPX("用户返回ack频率", 2);
	
	private String 	desc;
	private int 	type;
	public static final int TYPE_DELAY = 1;
	public static final int TYPE_QPX = 2;
	
	StatisDetailType(String desc, int type){
		this.desc = desc;
		this.type = type;
	}

	public int getType(){
		return type;
	}
	
	@Override
	public String toString(){
		return desc;
	}
}

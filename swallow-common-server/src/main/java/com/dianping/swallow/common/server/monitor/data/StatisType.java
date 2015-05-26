package com.dianping.swallow.common.server.monitor.data;

/**
 * @author mengwenchao
 *
 * 2015年5月22日 上午11:36:00
 */
public enum StatisType {
	
	SAVE,
	SEND,
	ACK;
	
	public StatisDetailType getQpxDetailType(){
		
		switch (this) {
			case SAVE:
				return StatisDetailType.SAVE_QPX;
			case SEND:
				return StatisDetailType.SEND_QPX;
			case ACK:
				return StatisDetailType.ACK_QPX;
			default:
				throw new IllegalStateException("unknown type:" + this);
		}
	}

	public StatisDetailType getDelayDetailType(){
		
		switch (this) {
			case SAVE:
				return StatisDetailType.SAVE_DELAY;
			case SEND:
				return StatisDetailType.SEND_DELAY;
			case ACK:
				return StatisDetailType.ACK_DELAY;
			default:
				throw new IllegalStateException("unknown type:" + this);
		}
	}
}

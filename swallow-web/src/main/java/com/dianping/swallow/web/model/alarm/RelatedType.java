package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 *         2015年8月5日 上午10:44:53
 */
public enum RelatedType {
	/**
	 * 
	 */
	P_SERVER_IP,
	/**
	 * 
	 */
	P_TOPIC,
	/**
	 * 
	 */
	P_IP,
	/**
	 * 
	 */
	C_SERVER_IP,
	/**
	 * 
	 */
	C_TOPIC,
	/**
	 * 
	 */
	C_CONSUMERID,
	/**
	 * 
	 */
	C_IP,
	/**
	 *
	 */
	K_SERVER_IP;

	public boolean isCConsumerId() {
		return this == C_CONSUMERID;
	}

	public boolean isCIp() {
		return this == C_IP;
	}

	public boolean isCTopic() {
		return this == C_TOPIC;
	}

	public boolean isCServerIp() {
		return this == C_SERVER_IP;
	}

	public boolean isPIp() {
		return this == P_IP;
	}

	public boolean isPTopic() {
		return this == P_TOPIC;
	}

	public boolean isPServerIp() {
		return this == P_SERVER_IP;
	}

	public boolean isKServerIp() {
		return this == K_SERVER_IP;
	}
}

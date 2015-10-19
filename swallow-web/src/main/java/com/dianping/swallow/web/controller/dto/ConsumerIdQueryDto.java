package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月15日下午4:49:22
 */
public class ConsumerIdQueryDto extends BaseQueryDto{
	
	private String consumerId;
	
	private String topic;
	
	private String consumerIp;
	
	private boolean inactive;
	
	public String getConsumerId() {
		return consumerId;
	}

	public String getTopic() {
		return topic;
	}

	public String getConsumerIp() {
		return consumerIp;
	}

	public boolean isInactive() {
		return inactive;
	}

	public boolean isInactive() {
		return inactive;
	}

	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}
	
}

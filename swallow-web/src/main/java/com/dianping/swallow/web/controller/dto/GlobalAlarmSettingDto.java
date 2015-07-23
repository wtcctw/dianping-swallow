package com.dianping.swallow.web.controller.dto;

/**
 * 
 * @author qiyin
 *
 */
public class GlobalAlarmSettingDto {

	private String swallowId;

	private String producerWhiteList;

	private String consumerWhiteList;
	
	public String getSwallowId() {
		return swallowId;
	}

	public void setSwallowId(String swallowId) {
		this.swallowId = swallowId;
	}

	public String getProducerWhiteList() {
		return producerWhiteList;
	}

	public void setProducerWhiteList(String producerWhiteList) {
		this.producerWhiteList = producerWhiteList;
	}

	public String getConsumerWhiteList() {
		return consumerWhiteList;
	}

	public void setConsumerWhiteList(String consumerWhiteList) {
		this.consumerWhiteList = consumerWhiteList;
	}

}

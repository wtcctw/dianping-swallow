package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月17日下午5:49:15
 */
public class TopicQueryDto extends BaseQueryDto{

	private String topic;
	
	private String proposal;
	
	private String producerServer;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getProposal() {
		return proposal;
	}

	public void setProposal(String proposal) {
		this.proposal = proposal;
	}

	public String getProducerServer() {
		return producerServer;
	}

	public void setProducerServer(String producerServer) {
		this.producerServer = producerServer;
	}

}

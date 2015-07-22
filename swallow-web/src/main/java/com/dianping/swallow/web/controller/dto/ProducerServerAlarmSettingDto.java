package com.dianping.swallow.web.controller.dto;


/**
 * 
 * @author mingdongli
 *
 * 2015年7月14日下午2:26:06
 */
public class ProducerServerAlarmSettingDto {
	
	private String serverId;
	
	private String whitelist;
	
	private long producerpeak;
	
	private long producervalley;
	
	private int producerfluctuation;
	
	private long fluctuationBase;

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getWhitelist() {
		return whitelist;
	}

	public void setWhitelist(String whitelist) {
		this.whitelist = whitelist;
	}

	public long getProducerpeak() {
		return producerpeak;
	}

	public void setProducerpeak(long producerpeak) {
		this.producerpeak = producerpeak;
	}

	public long getProducervalley() {
		return producervalley;
	}

	public void setProducervalley(long producervalley) {
		this.producervalley = producervalley;
	}

	public int getProducerfluctuation() {
		return producerfluctuation;
	}

	public void setProducerfluctuation(int producerfluctuation) {
		this.producerfluctuation = producerfluctuation;
	}

	public long getFluctuationBase() {
		return fluctuationBase;
	}

	public void setFluctuationBase(long fluctuationBase) {
		this.fluctuationBase = fluctuationBase;
	}
	

}

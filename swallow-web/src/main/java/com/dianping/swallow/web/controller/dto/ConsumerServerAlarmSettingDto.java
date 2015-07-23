package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年7月14日下午1:40:28
 */
public class ConsumerServerAlarmSettingDto {
	
	private String serverId;
	
	private String whitelist;
	
	private long consumersendpeak;
	
	private long consumersendvalley;
	
	private int consumersendfluctuation;
	
	private long sendFluctuationBase;
	
	private long consumerackpeak;
	
	private long consumerackvalley;
	
	private int consumerackfluctuation;
	
	private long ackFluctuationBase;

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

	public long getConsumersendpeak() {
		return consumersendpeak;
	}

	public void setConsumersendpeak(long consumersendpeak) {
		this.consumersendpeak = consumersendpeak;
	}

	public long getConsumersendvalley() {
		return consumersendvalley;
	}

	public void setConsumersendvalley(long consumersendvalley) {
		this.consumersendvalley = consumersendvalley;
	}

	public int getConsumersendfluctuation() {
		return consumersendfluctuation;
	}

	public void setConsumersendfluctuation(int consumersendfluctuation) {
		this.consumersendfluctuation = consumersendfluctuation;
	}

	public long getConsumerackpeak() {
		return consumerackpeak;
	}

	public void setConsumerackpeak(long consumerackpeak) {
		this.consumerackpeak = consumerackpeak;
	}

	public long getConsumerackvalley() {
		return consumerackvalley;
	}

	public void setConsumerackvalley(long consumerackvalley) {
		this.consumerackvalley = consumerackvalley;
	}

	public int getConsumerackfluctuation() {
		return consumerackfluctuation;
	}

	public void setConsumerackfluctuation(int consumerackfluctuation) {
		this.consumerackfluctuation = consumerackfluctuation;
	}

	public long getSendFluctuationBase() {
		return sendFluctuationBase;
	}

	public void setSendFluctuationBase(long sendFluctuationBase) {
		this.sendFluctuationBase = sendFluctuationBase;
	}

	public long getAckFluctuationBase() {
		return ackFluctuationBase;
	}

	public void setAckFluctuationBase(long ackFluctuationBase) {
		this.ackFluctuationBase = ackFluctuationBase;
	}

}

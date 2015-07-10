package com.dianping.swallow.web.model.statis;

public class ConsumerBaseStatsData {

	private long senderQpx;
	
	private long senderDelay;
	
	private long ackQpx;
	
	private long ackDelay;
	
	private long accumulation;

	public long getSenderQpx() {
		return senderQpx;
	}

	public void setSenderQpx(long senderQpx) {
		this.senderQpx = senderQpx;
	}

	public long getSenderDelay() {
		return senderDelay;
	}

	public void setSenderDelay(long senderDelay) {
		this.senderDelay = senderDelay;
	}

	public long getAckQpx() {
		return ackQpx;
	}

	public void setAckQpx(long ackQpx) {
		this.ackQpx = ackQpx;
	}

	public long getAckDelay() {
		return ackDelay;
	}

	public void setAckDelay(long ackDelay) {
		this.ackDelay = ackDelay;
	}

	public long getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(long accumulation) {
		this.accumulation = accumulation;
	}
}

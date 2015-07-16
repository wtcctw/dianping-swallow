package com.dianping.swallow.web.model.statis;
/**
 * 
 * @author qiyin
 *
 */
public class ConsumerBaseStatsData {

	private long sendQpx;
	
	private long sendDelay;
	
	private long ackQpx;
	
	private long ackDelay;
	
	private long accumulation;

	public long getSendQpx() {
		return sendQpx;
	}

	public void setSendQpx(long sendQpx) {
		this.sendQpx = sendQpx;
	}

	public long getSendDelay() {
		return sendDelay;
	}

	public void setSendDelay(long sendDelay) {
		this.sendDelay = sendDelay;
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

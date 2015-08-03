package com.dianping.swallow.web.model.stats;
/**
 * 
 * @author qiyin
 *
 * 2015年7月31日 下午3:56:45
 */
public abstract class ConsumerStatsData extends StatsData {
	
	private long sendQps;
	
	private long sendDelay;
	
	private long ackQps;
	
	private long ackDelay;
	
	private long accumulation;

	public long getSendQps() {
		return sendQps;
	}

	public void setSendQps(long sendQps) {
		this.sendQps = sendQps;
	}

	public long getSendDelay() {
		return sendDelay;
	}

	public void setSendDelay(long sendDelay) {
		this.sendDelay = sendDelay;
	}

	public long getAckQps() {
		return ackQps;
	}

	public void setAckQpx(long ackQpx) {
		this.ackQps = ackQpx;
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

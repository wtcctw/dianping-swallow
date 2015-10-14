package com.dianping.swallow.web.model.stats;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:56:45
 */
public abstract class ConsumerStatsData extends StatsData {

	private long sendQps;
	
	@Transient
	private long sendQpsTotal;

	private long sendDelay;

	@Transient
	private long ackQpsTotal;
	
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

	public void setAckQps(long ackQpx) {
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

	@Override
	public String toString() {
		return "ConsumerStatsData [sendQps=" + sendQps + ", sendDelay=" + sendDelay + ", ackQps=" + ackQps
				+ ", ackDelay=" + ackDelay + ", accumulation=" + accumulation + "]"+ super.toString();
	}

	@JsonIgnore
	public long getSendQpsTotal() {
		return sendQpsTotal;
	}

	@JsonIgnore
	public void setSendQpsTotal(long sendQpsTotal) {
		this.sendQpsTotal = sendQpsTotal;
	}

	@JsonIgnore
	public long getAckQpsTotal() {
		return ackQpsTotal;
	}
	
	@JsonIgnore
	public void setAckQpsTotal(long ackQpsTotal) {
		this.ackQpsTotal = ackQpsTotal;
	}

}

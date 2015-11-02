package com.dianping.swallow.web.model.stats;

import org.springframework.data.annotation.Transient;

import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.StatisType;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:56:45
 */
public abstract class ConsumerStatsData extends StatsData {
	
	public ConsumerStatsData() {
		eventType = EventType.CONSUMER;
	}

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
	
	public boolean checkSendQpsPeak(long expectQps) {
		return checkQpsPeak(getSendQps(), expectQps, StatisType.SENDQPS_PEAK);
	}

	public boolean checkSendQpsValley(long expectQps) {
		return checkQpsValley(getSendQps(), expectQps, StatisType.SENDQPS_VALLEY);
	}

	public boolean checkAckQpsPeak(long expectQps) {
		return checkQpsPeak(getAckQps(), expectQps, StatisType.ACKQPS_PEAK);
	}

	public boolean checkAckQpsValley(long expectQps) {
		return checkQpsValley(getAckQps(), expectQps, StatisType.ACKQPS_VALLEY);
	}

}

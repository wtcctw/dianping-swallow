package com.dianping.swallow.web.model.stats;
/**
 * 
 * @author qiyin
 *
 * 2015年7月31日 下午3:56:58
 */
public abstract class ProducerStatsData extends StatsData {

	private long qps;

	private long delay;

	public long getQps() {
		return qps;
	}

	public void setQps(long qps) {
		this.qps = qps;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	@Override
	public String toString() {
		return "ProducerStatsData [qpx=" + qps + ", delay=" + delay + "]";
	}

}

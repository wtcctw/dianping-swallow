package com.dianping.swallow.web.model.stats;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年9月6日 上午9:46:03
 */
public class ConsumerIpStatsData extends ConsumerStatsData {

	private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean checkPreStatsData(ConsumerIpStatsData statsData) {
		if (this.getSendQps() == 0L && this.getAckQps() == 0L && this.getSendDelay() == 0L && this.getAckDelay() == 0L) {
			if (this.getAccumulation() > 0) {
				return false;
			}
		}
		return true;
	}

	public boolean checkGroupStatsData(List<ConsumerIpStatsData> statsDatas) {
		if (this.getSendQps() == 0L && this.getAckQps() == 0L && this.getSendDelay() == 0L && this.getAckDelay() == 0L) {
			if (statsDatas != null && statsDatas.size() > 0) {
				for (ConsumerIpStatsData statsData : statsDatas) {
					if (StringUtils.equals(statsData.getIp(), this.ip)) {
						continue;
					} else {
						if (this.getSendQps() == 0L && this.getAckQps() == 0L && this.getSendDelay() == 0L
								&& this.getAckDelay() == 0L) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
}

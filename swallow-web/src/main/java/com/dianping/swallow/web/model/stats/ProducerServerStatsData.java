package com.dianping.swallow.web.model.stats;


import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:56:50
 */
@Document(collection = "PRODUCER_SERVER_STATS_DATA")
@CompoundIndexes({ @CompoundIndex(name = "IX_IP_TIMEKEY", def = "{'ip': -1, 'timeKey': 1}") })
public class ProducerServerStatsData extends ProducerStatsData {

	private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean checkQpsPeak(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() > expectQps) {
				report(eventFactory.createServerStatisEvent().setIp(ip), this.getQps(), expectQps,
						StatisType.SENDQPS_PEAK);
				return false;
			}
		}
		return true;
	}

	public boolean checkQpsValley(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() < expectQps) {
				report(eventFactory.createServerStatisEvent().setIp(ip), this.getQps(), expectQps,
						StatisType.SENDQPS_VALLEY);
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "ProducerServerStatsData [ip=" + ip + "]" + super.toString();
	}

}

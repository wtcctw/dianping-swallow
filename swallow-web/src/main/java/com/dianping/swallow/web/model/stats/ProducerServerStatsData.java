package com.dianping.swallow.web.model.stats;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.StatisEvent;

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

	@Override
	public StatisEvent createEvent() {
		return eventFactory.createServerStatisEvent().setIp(ip);
	}

	@Override
	public String toString() {
		return "ProducerServerStatsData [ip=" + ip + "]" + super.toString();
	}

}

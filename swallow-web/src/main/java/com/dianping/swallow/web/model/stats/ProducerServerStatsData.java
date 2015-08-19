package com.dianping.swallow.web.model.stats;

import java.util.Date;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:56:50
 */
@Document(collection = "PRODUCER_SERVER_STATS_DATA")
@CompoundIndexes({ @CompoundIndex(name = "IX_TIMEKEY_IP", def = "{'timeKey': 1, 'ip': -1}") })
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
				eventReporter.report(eventFactory.createServerStatisEvent().setIp(ip)
						.setCurrentValue(this.getQps()).setExpectedValue(expectQps)
						.setStatisType(StatisType.SENDQPS_PEAK).setCreateTime(new Date())
						.setEventType(EventType.PRODUCER));
				return false;
			}
		}
		return true;
	}

	public boolean checkQpsValley(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() < expectQps) {
				eventReporter.report(eventFactory.createServerStatisEvent().setIp(ip)
						.setCurrentValue(this.getQps()).setExpectedValue(expectQps)
						.setStatisType(StatisType.SENDQPS_VALLEY).setCreateTime(new Date())
						.setEventType(EventType.PRODUCER));
				return false;
			}
		}
		return true;
	}

}

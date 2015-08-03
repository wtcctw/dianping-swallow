package com.dianping.swallow.web.model.stats;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerStatisEvent;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:56:50
 */
@Service
@Scope("prototype")
@Document(collection = "ProducerServerStatsData")
@CompoundIndexes({ @CompoundIndex(name = "timeKey_ip_index", def = "{'timeKey': 1, 'ip': -1}") })
public class ProducerServerStatsData extends ProducerStatsData {

	private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean checkQps(long expectQps) {
		if (!checkQpsPeak(expectQps)) {
			return false;
		}
		if (!checkQpsValley(expectQps)) {
			return false;
		}
		return true;
	}

	public boolean checkQpsPeak(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() > expectQps) {
				eventReporter.report(new ServerStatisEvent().setIp(ip).setCurrentValue(this.getQps())
						.setExpectedValue(expectQps).setStatisType(StatisType.SENDQPS_PEAK)
						.setCreateTime(new Date()).setEventType(EventType.PRODUCER));
				return false;
			}
		}
		return true;
	}

	public boolean checkQpsValley(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() < expectQps) {
				eventReporter.report(new ServerStatisEvent().setIp(ip).setCurrentValue(this.getQps())
						.setExpectedValue(expectQps).setStatisType(StatisType.SENDQPS_VALLEY)
						.setCreateTime(new Date()).setEventType(EventType.PRODUCER));
				return false;
			}
		}
		return true;
	}

}

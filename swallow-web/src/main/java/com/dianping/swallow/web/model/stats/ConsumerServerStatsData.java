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
 *         2015年7月31日 下午3:56:39
 */
@Service
@Scope("prototype")
@Document(collection = "ConsumerServerStatsData")
@CompoundIndexes({ @CompoundIndex(name = "timeKey_ip_index", def = "{'timeKey': 1, 'ip': -1}") })
public class ConsumerServerStatsData extends ConsumerStatsData {

	private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean checkSendQps(long expectQps) {
		if (!checkQpsPeak(expectQps, StatisType.SENDQPS_PEAK)) {
			return false;
		}
		if (!checkQpsValley(expectQps, StatisType.SENDQPS_VALLEY)) {
			return false;
		}
		return true;
	}

	public boolean checkAckQps(long expectQps) {
		if (!checkQpsPeak(expectQps, StatisType.ACKQPS_PEAK)) {
			return false;
		}
		if (!checkQpsValley(expectQps, StatisType.ACKQPS_VALLEY)) {
			return false;
		}
		return true;
	}

	private boolean checkQpsPeak(long expectQps, StatisType statisType) {
		if (this.getSendQps() != 0L) {
			if (this.getSendQps() > expectQps) {
				eventReporter.report(new ServerStatisEvent().setIp(ip).setCurrentValue(this.getSendQps())
						.setExpectedValue(expectQps).setStatisType(statisType).setCreateTime(new Date())
						.setEventType(EventType.CONSUMER));
				return false;
			}
		}
		return true;
	}

	private boolean checkQpsValley(long expectQps, StatisType statisType) {
		if (this.getSendQps() != 0L) {
			if (this.getSendQps() < expectQps) {
				eventReporter.report(new ServerStatisEvent().setIp(ip).setCurrentValue(this.getSendQps())
						.setExpectedValue(expectQps).setStatisType(statisType).setCreateTime(new Date())
						.setEventType(EventType.CONSUMER));
				return false;
			}
		}
		return true;
	}

}

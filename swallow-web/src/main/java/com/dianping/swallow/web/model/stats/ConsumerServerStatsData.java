package com.dianping.swallow.web.model.stats;

import java.util.Date;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:56:39
 */
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

	public boolean checkSendQpsPeak(long expectQps) {
		return checkQpsPeak(this.getSendQps(), expectQps, StatisType.SENDQPS_PEAK);
	}

	public boolean checkSendQpsValley(long expectQps){
		return checkQpsValley(this.getSendQps(), expectQps, StatisType.SENDQPS_VALLEY);
	}

	public boolean checkAckQpsPeak(long expectQps) {
		return checkQpsPeak(this.getAckQps(), expectQps, StatisType.ACKQPS_PEAK);
	}

	public boolean checkAckQpsValley(long expectQps) {
		return checkQpsValley(this.getAckQps(), expectQps, StatisType.ACKQPS_VALLEY);
	}

	public boolean checkQpsPeak(long qps, long expectQps, StatisType statisType) {
		if (qps != 0L) {
			if (qps > expectQps) {
				eventReporter.report(EventFactory.getInstance().createServerStatisEvent().setIp(ip)
						.setCurrentValue(qps).setExpectedValue(expectQps).setStatisType(statisType)
						.setCreateTime(new Date()).setEventType(EventType.CONSUMER));
				return false;
			}
		}
		return true;
	}

	public boolean checkQpsValley(long qps, long expectQps, StatisType statisType) {
		if (qps != 0L) {
			if (qps < expectQps) {
				eventReporter.report(EventFactory.getInstance().createServerStatisEvent().setIp(ip)
						.setCurrentValue(qps).setExpectedValue(expectQps).setStatisType(statisType)
						.setCreateTime(new Date()).setEventType(EventType.CONSUMER));
				return false;
			}
		}
		return true;
	}

}

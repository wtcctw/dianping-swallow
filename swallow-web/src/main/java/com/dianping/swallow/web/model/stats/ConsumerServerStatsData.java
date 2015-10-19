package com.dianping.swallow.web.model.stats;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:56:39
 */
@Document(collection = "CONSUMER_SERVER_STATS_DATA")
@CompoundIndexes({ @CompoundIndex(name = "IX_IP_TIMEKEY", def = "{ 'ip': -1, 'timeKey': 1}") })
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

	public boolean checkSendQpsValley(long expectQps) {
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
				report(eventFactory.createServerStatisEvent().setIp(ip), qps, expectQps, statisType);
				return false;
			}
		}
		return true;
	}

	public boolean checkQpsValley(long qps, long expectQps, StatisType statisType) {
		if (qps != 0L) {
			if (qps < expectQps) {
				report(eventFactory.createServerStatisEvent().setIp(ip), qps, expectQps, statisType);
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "ConsumerServerStatsData [ip=" + ip + "]" + super.toString();
	}

}

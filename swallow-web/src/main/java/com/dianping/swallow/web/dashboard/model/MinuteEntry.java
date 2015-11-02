package com.dianping.swallow.web.dashboard.model;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author mingdongli
 *
 *         2015年7月7日上午9:36:34
 */
@Document(collection = "DASHBOARD_STATS_DATA")
public class MinuteEntry {

	@Indexed(name = "IX_TIME", direction = IndexDirection.ASCENDING)
	private Date time;

	private List<Entry> comprehensiveList;

	private List<Entry> sendList;

	private List<Entry> ackList;

	private List<Entry> accuList;

	public MinuteEntry() {

	}

	public Date getTime() {

		return time;
	}

	public MinuteEntry setTime(Date time) {

		this.time = time;
		return this;
	}

	public void setComprehensiveList(List<Entry> comprehensiveList) {
		this.comprehensiveList = comprehensiveList;
	}

	public void setSendList(List<Entry> sendList) {
		this.sendList = sendList;
	}

	public void setAckList(List<Entry> ackList) {
		this.ackList = ackList;
	}

	public void setAccuList(List<Entry> accuList) {
		this.accuList = accuList;
	}

	public List<Entry> getListByType(DashboardEnum dashboardEnum) {

		if (dashboardEnum == DashboardEnum.COMPREHENSIVE) {
			return comprehensiveList;
		} else if (dashboardEnum == DashboardEnum.SEND) {
			return sendList;
		} else if (dashboardEnum == DashboardEnum.ACK) {
			return ackList;
		} else {
			return accuList;
		}
	}

}

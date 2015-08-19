package com.dianping.swallow.web.dashboard.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @author mingdongli
 *
 * 2015年7月7日上午9:36:34
 */
@Document(collection = "Dashboard")
public class MinuteEntry {
	
	@Indexed
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
	
	public List<Entry> getComprehensiveList() {
		return comprehensiveList;
	}

	public void setComprehensiveList(List<Entry> comprehensiveList) {
		this.comprehensiveList = comprehensiveList;
	}

	public List<Entry> getSendList() {
		return sendList;
	}

	public void setSendList(List<Entry> sendList) {
		this.sendList = sendList;
	}

	public List<Entry> getAckList() {
		return ackList;
	}

	public void setAckList(List<Entry> ackList) {
		this.ackList = ackList;
	}

	public List<Entry> getAccuList() {
		return accuList;
	}

	public void setAccuList(List<Entry> accuList) {
		this.accuList = accuList;
	}

	public List<Entry> getListByType(DashboardEnum dashboardEnum){
		
		if(dashboardEnum == DashboardEnum.COMPREHENSIVE){
			return comprehensiveList;
		}else if(dashboardEnum == DashboardEnum.SEND){
			return sendList;
		}else if(dashboardEnum == DashboardEnum.ACK){
			return ackList;
		}else{
			return accuList;
		}
	}

}


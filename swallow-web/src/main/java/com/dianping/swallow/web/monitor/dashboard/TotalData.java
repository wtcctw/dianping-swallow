package com.dianping.swallow.web.monitor.dashboard;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mingdongli
 *
 * 2015年7月7日下午8:12:40
 */
public class TotalData {
	
	private String server;
	
	private String topic;
	
	private String cid;
	
	private String email;
	
	private String dpMobile;
	
	private String time;
	
	List<Long> listSend = new ArrayList<Long>();

	List<Long> listAck = new ArrayList<Long>();
	
	List<Long> listAccu = new ArrayList<Long>();
	
	List<Entry> entrys = new ArrayList<Entry>();
	
	public List<Entry> getEntrys() {
		return entrys;
	}

	public TotalData setEntrys(List<Entry> entrys) {
		this.entrys = entrys;
		return this;
	}
	
	public String getServer() {
		return server;
	}

	public TotalData setServer(String server) {
		this.server = server;
		return this;
	}

	public String getTopic() {
		return topic;
	}

	public TotalData setTopic(String topic) {
		this.topic = topic;
		return this;
	}

	public String getCid() {
		return cid;
	}

	public TotalData setCid(String cid) {
		this.cid = cid;
		return this;
	}
	
	public String getEmail() {
		return email;
	}

	public TotalData setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getDpMobile() {
		return dpMobile;
	}

	public TotalData setDpMobile(String dpMobile) {
		this.dpMobile = dpMobile;
		return this;
	}

	public List<Long> getListSend() {
		return listSend;
	}

	public TotalData setListSend(List<Long> listSend) {
		this.listSend = listSend;
		return this;
	}

	public List<Long> getListAck() {
		return listAck;
	}

	public TotalData setListAck(List<Long> listAck) {
		this.listAck = listAck;
		return this;
	}

	public List<Long> getListAccu() {
		return listAccu;
	}

	public TotalData setListAccu(List<Long> listAccu) {
		this.listAccu = listAccu;
		return this;
	}

	public String getTime() {
		return time;
	}

	public TotalData setTime(String time) {
		this.time = time;
		return this;
	}

	@Override
	public String toString() {
		return "TotalData [server=" + server + ", topic=" + topic + ", cid=" + cid + ", email=" + email + ", dpMobile="
				+ dpMobile + ", time=" + time + ", listSend=" + listSend + ", listAck=" + listAck + ", listAccu="
				+ listAccu + ", entrys=" + entrys + "]";
	}

}

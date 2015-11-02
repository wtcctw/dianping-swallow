package com.dianping.swallow.web.dashboard.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mingdongli
 *
 * 2015年7月7日下午8:12:40
 */
public class TotalData {
	
	private String server;
	
	List<Long> listSend = new ArrayList<Long>();

	List<Long> listAck = new ArrayList<Long>();
	
	List<Long> listAccu = new ArrayList<Long>();
	
	public String getServer() {
		return server;
	}

	public TotalData setServer(String server) {
		this.server = server;
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

}

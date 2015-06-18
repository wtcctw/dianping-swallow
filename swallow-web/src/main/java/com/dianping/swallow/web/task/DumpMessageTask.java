package com.dianping.swallow.web.task;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.dao.MessageDao;


/**
 * @author mingdongli
 *
 * 2015年6月16日下午5:57:38
 */
@Component
public class DumpMessageTask implements Callable<Integer>{
	
	private String topic;
	
	private String startdt;

	private String stopdt;
	
	private String filename;

	private MessageDao webMessageDao;
    
    public MessageDao getWebMessageDao() {
		return webMessageDao;
	}

	public DumpMessageTask setWebMessageDao(MessageDao webMessageDao) {
		this.webMessageDao = webMessageDao;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public DumpMessageTask setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	public String getTopic() {
		return topic;
	}

	public DumpMessageTask setTopic(String topic) {
		this.topic = topic;
		return this;
	}

	public String getStartdt() {
		return startdt;
	}

	public DumpMessageTask setStartdt(String startdt) {
		this.startdt = startdt;
		return this;
	}

	public String getStopdt() {
		return stopdt;
	}

	public DumpMessageTask setStopdt(String stopdt) {
		this.stopdt = stopdt;
		return this;
	}

	@Override
	public Integer call() throws Exception {
		
		int status = webMessageDao.exportMessages(topic, startdt, stopdt, filename);
		return status;
	}      
}   
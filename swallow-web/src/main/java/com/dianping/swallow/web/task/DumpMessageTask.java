package com.dianping.swallow.web.task;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.web.controller.MessageDumpController;
import com.dianping.swallow.web.dao.MessageDao;
import com.dianping.swallow.web.service.MessageDumpService;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * @author mingdongli
 *
 *         2015年6月16日下午5:57:38
 */
public class DumpMessageTask implements Runnable {

	private String topic;

	private String startdt;

	private String stopdt;

	private String filename;

	private MessageDao webMessageDao;
	
	private MessageDumpService messageDumpService;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public MessageDumpService getMessageDumpService() {
		return messageDumpService;
	}

	public DumpMessageTask setMessageDumpService(MessageDumpService messageDumpService) {
		this.messageDumpService = messageDumpService;
		return this;
	}

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
	public void run() {
		System.out.println("beginnnnn");
		List<DBObject> dboList = webMessageDao.exportMessages(topic, startdt, stopdt);
		System.out.println(String.format("dboList size is %d", dboList.size()));
		int status = writeContentToFile(filename, dboList);
		if(status == 0){
			try{
				int n = messageDumpService.updateDumpMessageStatus(filename, true);
				if(n == 1){
					logger.info(String.format("Update file %s status to true successfully", filename));
				}else{
					logger.info(String.format("Update file %s status to true failed with n equal to %d", filename, n));
				}
			}catch(MongoException e){
				logger.info("MongoException when update messagedump", e);
			}
		}
	}

	private int writeContentToFile(String filename, List<DBObject> dboList) {
		GZIPOutputStream gos = null;
		BufferedWriter writer = null;
		try {
			gos = new GZIPOutputStream(new FileOutputStream(MessageDumpController.FILEPATH + filename, true));
			writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
			for (DBObject dbo : dboList) {
				writer.append(dbo.toString());
				writer.newLine();
				writer.flush();
			}
			return ResponseStatus.SUCCESS.getStatus();
		} catch (IOException e) {
			logger.error("Open output stream error", e);
			return ResponseStatus.IOEXCEPTION.getStatus();
		} finally {
			try {
				writer.close();
				gos.close();
			} catch (IOException e) {
				logger.error("Open output stream error", e);
				return ResponseStatus.IOEXCEPTION.getStatus();
			}
		}

	}
}
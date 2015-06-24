package com.dianping.swallow.web.task;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.DumpMessageController;
import com.dianping.swallow.web.dao.MessageDao;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.DBObject;

/**
 * @author mingdongli
 *
 *         2015年6月16日下午5:57:38
 */
@Component
public class DumpMessageTask implements Callable<Integer> {

	private String topic;

	private String startdt;

	private String stopdt;

	private String filename;

	private MessageDao webMessageDao;

	private final Logger logger = LoggerFactory.getLogger(getClass());

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
		List<DBObject> dboList = webMessageDao.exportMessages(topic, startdt, stopdt);
		int status = writeContentToFile(filename, dboList);
		return status;
	}

	private int writeContentToFile(String filename, List<DBObject> dboList) {
		GZIPOutputStream gos = null;
		BufferedWriter writer = null;
		try {
			gos = new GZIPOutputStream(new FileOutputStream(DumpMessageController.FILEPATH + filename, true));
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
				gos.close();
			} catch (IOException e) {
				logger.error("Open output stream error", e);
				return ResponseStatus.IOEXCEPTION.getStatus();
			}
		}

	}
}
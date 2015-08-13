package com.dianping.swallow.web.task;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang.StringUtils;
import org.bson.types.BSONTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.util.ZipUtil;
import com.dianping.swallow.web.controller.MessageDumpController;
import com.dianping.swallow.web.dao.impl.DefaultMessageDao;
import com.dianping.swallow.web.service.MessageDumpService;
import com.dianping.swallow.web.service.MessageService;
import com.dianping.swallow.web.service.impl.MessageServiceImpl;
import com.mongodb.DBCursor;
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

	private MessageService messageService;

	private MessageDumpService messageDumpService;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public MessageDumpService getMessageDumpService() {
		return messageDumpService;
	}

	public DumpMessageTask setMessageDumpService(MessageDumpService messageDumpService) {
		this.messageDumpService = messageDumpService;
		return this;
	}

	public MessageService getMessageService() {
		return messageService;
	}

	public DumpMessageTask setMessageService(MessageService messageService) {
		this.messageService = messageService;
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

		Map<String, Object> result = messageService.exportMessage(topic, startdt, stopdt);

		GZIPOutputStream gos = null;
		BufferedWriter writer = null;
		try {
			gos = new GZIPOutputStream(new FileOutputStream(MessageDumpController.FILEPATH + filename, true));
			writer = new BufferedWriter(new OutputStreamWriter(gos, "UTF-8"));
		} catch (Exception e) {
			logger.error("open io error", e);
		} 
		String laststring = null;
		String firststring = null;
		int maxsize = (Integer) result.get("maxsize");
		int total = (Integer) result.get("total");
		DBCursor cursor = (DBCursor) result.get("message");
		int size = Math.min(maxsize, total);
		int iterator = 0;
        while (cursor.hasNext()) {
        	DBObject dbo = cursor.next();
        	String content = (String) dbo.get("c");
        	if(StringUtils.isNotBlank(content) && content.startsWith(MessageServiceImpl.GZIP)){
        		try {
					content = ZipUtil.unzip(content);
					dbo.put("c", content);
				} catch (IOException e) {
					if(logger.isErrorEnabled()){
						logger.error("Error when unzip message content.", e);
					}
				}
        	}
        	try {
				writer.append(dbo.toString());
				writer.newLine();
			} catch (IOException e) {
				logger.error("Operator io error", e);
			}
        	iterator++;
        	if(iterator == 1){
        		BSONTimestamp firsttime = (BSONTimestamp) dbo.get(DefaultMessageDao.ID);
        		firststring = BSONTimestampToString(firsttime);
        	}
        	
        	if(iterator == size){
        		BSONTimestamp lasttime = (BSONTimestamp) dbo.get(DefaultMessageDao.ID);
        		laststring = BSONTimestampToString(lasttime);
        		try {
					writer.flush();
					break;
				} catch (IOException e) {
					logger.error("Operator io error", e);
				} finally {
					try {
						writer.close();
						gos.close();
					} catch (IOException e) {
						logger.error("Operator io error", e);
					}
				} 
        	}else if(iterator % 10000 == 0){
        		try {
					writer.flush();
				} catch (IOException e) {
					logger.error("Operator io error", e);
				}
        	}
        }

		try {
			int n = messageDumpService.updateDumpMessage(filename, true, total, size,firststring, laststring);
			if (n > 0) {
				logger.info(String.format("Update file %s status to true successfully", filename));
			} else {
				logger.info(String.format("Update file %s status to true failed with n equal to %d", filename, n));
			}
			messageDumpService.execBlockingDumpMessageTask(topic);
		} catch (MongoException e) {
			logger.info("MongoException when update messagedump", e);
		}
	}


	private String BSONTimestampToString(BSONTimestamp ts) {
		int seconds = ts.getTime();
		long millions = new Long(seconds) * 1000;
		return new SimpleDateFormat(DefaultMessageDao.TIMEFORMAT).format(new Date(millions));
	}
}
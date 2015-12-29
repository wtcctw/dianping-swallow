package com.dianping.swallow.web.task;

import com.dianping.swallow.common.internal.util.ZipUtil;
import com.dianping.swallow.web.controller.MessageDumpController;
import com.dianping.swallow.web.dao.impl.DefaultMessageDao;
import com.dianping.swallow.web.model.MessageDump;
import com.dianping.swallow.web.service.MessageDumpService;
import com.dianping.swallow.web.service.MessageService;
import com.dianping.swallow.web.service.impl.MessageServiceImpl;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import org.apache.commons.lang.StringUtils;
import org.bson.types.BSONTimestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * @author mingdongli
 *
 *         2015年6月16日下午5:57:38
 */
public class DumpMessageTask implements Runnable {

	private static final String DELIMITOR = "|";

	private String topic;

	private Date startdt;

	private Date stopdt;

	private String filename;

	private MessageService messageService;

	private MessageDumpService messageDumpService;

	private final Logger logger = LogManager.getLogger(getClass());

	public DumpMessageTask setMessageDumpService(MessageDumpService messageDumpService) {
		this.messageDumpService = messageDumpService;
		return this;
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

	public Date getStartdt() {
		return startdt;
	}

	public DumpMessageTask setStartdt(Date startdt) {
		this.startdt = startdt;
		return this;
	}

	public Date getStopdt() {
		return stopdt;
	}

	public DumpMessageTask setStopdt(Date stopdt) {
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
		Date laststring = null;
		Date firststring = null;
		int maxsize = (Integer) result.get("maxsize");
		int total = (Integer) result.get("total");
		DBCursor cursor = (DBCursor) result.get("message");
		int size = Math.min(maxsize, total);
		int iterator = 0;
		while (cursor.hasNext()) {
			DBObject dbo = cursor.next();
			String content = (String) dbo.get("c");
			if (StringUtils.isNotBlank(content) && content.startsWith(MessageServiceImpl.GZIP)) {
				try {
					content = ZipUtil.unzip(content);
					dbo.put("c", content);
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
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
			if (iterator == 1) {
				BSONTimestamp firsttime = (BSONTimestamp) dbo.get(DefaultMessageDao.ID);
				firststring = BSONTimestampToDate(firsttime);
			}

			if (iterator == size) {
				BSONTimestamp lasttime = (BSONTimestamp) dbo.get(DefaultMessageDao.ID);
				laststring = BSONTimestampToDate(lasttime);
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
			} else if (iterator % 10000 == 0) {
				try {
					writer.flush();
				} catch (IOException e) {
					logger.error("Operator io error", e);
				}
			}
		}

		try {
			MessageDump messageDump = new MessageDump();
			StringBuffer sb = new StringBuffer();
			sb.append(size).append(DELIMITOR).append(total).append(DELIMITOR).append(firststring).append(DELIMITOR)
					.append(laststring);
			messageDump.setFilename(filename).setStartdt(firststring).setStopdt(laststring).setFinished(Boolean.TRUE)
					.setDesc(sb.toString());
			int n = messageDumpService.updateDumpMessage(messageDump);
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

	private Date BSONTimestampToDate(BSONTimestamp ts) {
		int seconds = ts.getTime();
		long millions = new Long(seconds) * 1000;
		return new Date(millions);
	}
}
package com.dianping.swallow.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.dao.MessageDumpDao;
import com.dianping.swallow.web.model.MessageDump;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MessageDumpService;
import com.dianping.swallow.web.service.MessageService;
import com.dianping.swallow.web.task.DumpMessageTask;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;

/**
 * @author mingdongli
 *
 *         2015年6月25日下午12:03:33
 */
@Service("messageDumpService")
public class MessageDumpServiceImpl extends AbstractSwallowService implements MessageDumpService {

	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String DELIMITOR = "|";

	@Autowired
	private MessageDumpDao messageDumpDao;

	@Resource(name = "messageService")
	private MessageService messageService;

	ExecutorService exec = Executors.newFixedThreadPool(CommonUtils.getCpuCount());

	Map<String, LinkedBlockingQueue<Runnable>> tasks = new ConcurrentHashMap<String, LinkedBlockingQueue<Runnable>>();

	@Override
	public Map<String, Object> loadSpecificDumpMessage(int start, int span, String topic) {

		return messageDumpDao.loadSpecifitMessageDump(start, span, topic);
	}

	@Override
	public int saveDumpMessage(String topic, String name, String startdt, String stopdt, String filename,
			boolean finished) {

		MessageDump md = new MessageDump();

		Long id = System.currentTimeMillis();

		String date = new SimpleDateFormat(TIMEFORMAT).format(new Date());
		md.set_id(id.toString()).setTopic(topic).setName(name).setTime(date).setStartdt(startdt).setStopdt(stopdt)
				.setFilename(filename).setFinished(finished).setDesc("");
		int status = messageDumpDao.saveMessageDump(md);

		return status;
	}

	@Override
	public MessageDump loadDumpMessage(String filename) {

		return messageDumpDao.loadMessageDump(filename);
	}

	@Override
	public int updateDumpMessage(String filename, boolean finished, int count, int size, String firsttime,
			String laststring) {

		StringBuffer sb = new StringBuffer();
		sb.append(size).append(DELIMITOR).append(count).append(DELIMITOR).append(firsttime).append(DELIMITOR)
				.append(laststring);
		return messageDumpDao.updateMessageDumpStatus(filename, finished, sb.toString());
	}

	@Override
	public int removeDumpMessage(String filename) throws MongoException {

		return messageDumpDao.removeMessageDump(filename);
	}

	@Override
	public List<MessageDump> loadAllDumpMessage() {

		return messageDumpDao.loadAllMessageDumps();
	}

	@Override
	public MessageDump loadUnfinishedDumpMessage(String topic) {

		return messageDumpDao.loadUnfinishedMessageDump(topic);
	}

	@Override
	public int execDumpMessageTask(String topic, String startdt, String stopdt, String filename, String username) {

		DumpMessageTask fileDownloadTask = new DumpMessageTask();
		fileDownloadTask.setTopic(topic).setStartdt(startdt).setStopdt(stopdt).setFilename(filename)
				.setMessageService(messageService).setMessageDumpService(this);

		if (loadUnfinishedDumpMessage(topic) != null) {
			LinkedBlockingQueue<Runnable> lbq = tasks.get(topic);
			if (lbq == null) {
				lbq = new LinkedBlockingQueue<Runnable>(5);
			}
			try {
				lbq.add(fileDownloadTask);
				logger.info(String.format("Add dump message task %s to blocking queue", fileDownloadTask.toString()));
			} catch (Exception e) {
				logger.error("LinkedBlockingQueue's size is 5, no space for extra task", e);
			}
			saveDumpMessage(topic, username, startdt, stopdt, filename, false);
			tasks.put(topic, lbq);
		} else {
			saveDumpMessage(topic, username, startdt, stopdt, filename, false);
			exec.submit(fileDownloadTask);
			logger.info(String.format("Start download task for %s to export messages from %s to %s", topic, startdt,
					stopdt));
		}

		return ResponseStatus.SUCCESS.getStatus();
	}

	@Override
	public void execBlockingDumpMessageTask(String topicName) {
		LinkedBlockingQueue<Runnable> lbq = tasks.get(topicName);
		if (lbq != null && !lbq.isEmpty()) {
			Runnable dmt = lbq.poll();
			logger.info(String.format("poll dump message task %s out of blocking queue", dmt.toString()));
			exec.submit(dmt);
		}
	}

}

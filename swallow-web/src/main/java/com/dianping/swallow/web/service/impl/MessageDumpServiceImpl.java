package com.dianping.swallow.web.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.MessageDumpController;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
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
public class MessageDumpServiceImpl extends AbstractSwallowService implements MessageDumpService,
		ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private MessageDumpDao messageDumpDao;

	@Resource(name = "messageService")
	private MessageService messageService;

	ExecutorService exec = Executors.newFixedThreadPool(CommonUtils.getCpuCount());

	Map<String, LinkedBlockingQueue<Runnable>> tasks = new ConcurrentHashMap<String, LinkedBlockingQueue<Runnable>>();

	@Override
	public Pair<Long, List<MessageDump>> loadDumpMessagePage(TopicQueryDto topicQueryDto) {

		return messageDumpDao.loadMessageDumpPageByTopic(topicQueryDto);
	}

	@Override
	public ResponseStatus saveDumpMessage(MessageDump md) {

		Long id = System.currentTimeMillis();

		md.set_id(id.toString()).setTime(new Date()).setDesc("");
		
		return messageDumpDao.saveMessageDump(md);

	}

	@Override
	public MessageDump loadDumpMessage(String filename) {

		return messageDumpDao.loadMessageDump(filename);
	}

	@Override
	public int updateDumpMessage(MessageDump messageDump) {

		return messageDumpDao.updateMessageDump(messageDump);
	}

	@Override
	public int removeDumpMessage(String filename) throws MongoException {

		return messageDumpDao.removeMessageDump(filename);
	}

	@Override
	public Pair<Long, List<MessageDump>> loadAllDumpMessage() {

		return messageDumpDao.loadAllMessageDumps();
	}

	@Override
	public MessageDump loadUnfinishedDumpMessage(String topic) {

		return messageDumpDao.loadUnfinishedMessageDump(topic);
	}

	@Override
	public ResponseStatus execDumpMessageTask(MessageDump messageDump) {

		DumpMessageTask fileDownloadTask = new DumpMessageTask();
		String topic = messageDump.getTopic();
		Date startdt = messageDump.getStartdt();
		Date stopdt = messageDump.getStopdt();
		String filename = messageDump.getFilename();
		fileDownloadTask.setTopic(topic).setStartdt(startdt).setStopdt(stopdt).setFilename(filename)
				.setMessageService(messageService).setMessageDumpService(this);

		if (loadUnfinishedDumpMessage(topic) != null) {
			LinkedBlockingQueue<Runnable> lbq = tasks.get(topic);
			if (lbq == null) {
				lbq = new LinkedBlockingQueue<Runnable>(5);
			}
			try {
				lbq.add(fileDownloadTask);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Add dump message task %s to blocking queue", fileDownloadTask.toString()));
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("LinkedBlockingQueue's size is 5, no space for extra task", e);
				}
			}
			saveDumpMessage(messageDump);
			tasks.put(topic, lbq);
		} else {
			saveDumpMessage(messageDump);
			exec.submit(fileDownloadTask);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Start download task for %s to export messages from %s to %s", topic,
						startdt, stopdt));
			}
		}

		return ResponseStatus.SUCCESS;
	}

	@Override
	public void execBlockingDumpMessageTask(String topicName) {

		LinkedBlockingQueue<Runnable> lbq = tasks.get(topicName);

		if (lbq != null && !lbq.isEmpty()) {
			Runnable dmt = lbq.poll();
			if (logger.isInfoEnabled()) {
				logger.info(String.format("poll dump message task %s out of blocking queue", dmt.toString()));
			}
			exec.submit(dmt);
		}

	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (event.getApplicationContext().getParent() == null) {
			List<MessageDump> mds = loadAllDumpMessage().getSecond();

			for (MessageDump md : mds) {
				if (!md.isFinished()) {
					String f = md.getFilename();
					File file = new File(MessageDumpController.FILEPATH + f);
					try {
						if (!file.exists()) {
							file.createNewFile();
						}
						FileWriter fileWriter = new FileWriter(file);
						fileWriter.write("");
						fileWriter.flush();
						fileWriter.close();
					} catch (IOException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Create filewriter error", e);
						}
					}
					String topic = md.getTopic();
					String filename = md.getFilename();
					removeDumpMessage(filename);
					execDumpMessageTask(md);

					if (logger.isInfoEnabled()) {
						logger.info(String.format("Start export message of %s to file %s", topic, f));
					}
				}
			}
		}
	}

}

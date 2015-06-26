package com.dianping.swallow.web.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.MessageDumpDao;
import com.dianping.swallow.web.model.MessageDump;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MessageDumpService;
import com.dianping.swallow.web.service.MessageService;
import com.mongodb.MongoException;

/**
 * @author mingdongli
 *
 *         2015年6月25日下午12:03:33
 */
@Service("messageDumpService")
public class MessageDumpServiceImpl extends AbstractSwallowService implements MessageDumpService {

	private static final String TIMEFORMAT = "yyyy-MM-dd HH:mm:ss";

	@Autowired
	private MessageDumpDao messageDumpDao;

	@Resource(name = "messageService")
	private MessageService messageService;

	boolean allFinished = false;

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
				.setFilename(filename).setFinished(finished);
		int status = messageDumpDao.saveMessageDump(md);

		return status;
	}

	@Override
	public MessageDump loadDumpMessage(String filename) {

		return messageDumpDao.loadMessageDump(filename);
	}

	@Override
	public int updateDumpMessageStatus(String filename, boolean finished) {

		return messageDumpDao.updateMessageDumpStatus(filename, finished);
	}

	@Override
	public int removeDumpMessage(String filename) throws MongoException {

		return messageDumpDao.removeMessageDump(filename);
	}

	@Override
	public List<MessageDump> loadAllDumpMessage() {
		
		return messageDumpDao.loadAllMessageDumps();
	}

}

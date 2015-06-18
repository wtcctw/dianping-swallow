package com.dianping.swallow.web.service.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.MessageDao;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.DumpMessageService;
import com.dianping.swallow.web.task.DumpMessageTask;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年6月16日下午5:38:45
 */
@Service("fileDownloadService")
public class DumpMessageServiceImpl extends AbstractSwallowService implements DumpMessageService {
	
	@Autowired
	private MessageDao webMessageDao;  //must define here and set for DumpMessageTask

	@Override
	public Integer exportMessageByTimeSpan(String topicName, String startdt, String stopdt, String filename) {
		
		ExecutorService exec = Executors.newFixedThreadPool(1);
		DumpMessageTask fileDownloadTask = new DumpMessageTask();
		fileDownloadTask.setTopic(topicName).setStartdt(startdt).setStopdt(stopdt).setFilename(filename).setWebMessageDao(webMessageDao);
		FutureTask<Integer> futureTask = new FutureTask<Integer>(fileDownloadTask);
		exec.submit(futureTask);
        try {
			int result = futureTask.get();  //block
			exec.shutdown();
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return ResponseStatus.E_INTERRUPTEDEXCEPTION;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return ResponseStatus.E_RUNTIMEEXCEPTION;
		}
	}

}

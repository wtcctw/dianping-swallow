package com.dianping.swallow.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.internal.FileQueueHolder;
import com.dianping.swallow.producer.impl.internal.HandlerAsynchroSeparatelyMode;

/**
 * @author mengwenchao
 *
 * 2015年5月15日 上午10:18:02
 */
public class AsyncTest extends AbstractConsumerTest{
	
	protected final int messageCount = 1000;
	
	private final String fileQueueBaseDir = "/data/appdatas/filequeue-test"; 
	
	private File queue;

	private ProducerMode  mode = ProducerMode.ASYNC_MODE;
	
	private int sendMessageSize = 1024;
	
	@Before
	public void beforeAsyncTest() throws IOException{
		
		FileUtils.deleteDirectory(new File(fileQueueBaseDir));
		
		queue = new File(fileQueueBaseDir, topic);
		FileQueueHolder.removeQueue(topic);
	}
	
	@Test
	public void testAsync() throws SendFailedException, RemoteServiceInitFailedException{
		
		mode = ProducerMode.ASYNC_MODE;
		runAsync();
	}
	
	@Test
	public void testMultiMessageFile() throws IllegalArgumentException, IllegalAccessException, SendFailedException, RemoteServiceInitFailedException{

		Long fileSize = 1024L;
		Long previous = setFileSize(fileSize);
		try{
			sendMessageSize = fileSize.intValue();
			runAsync();
			
			judgeFileName();
		}finally{
			setFileSize(previous);
		}
	}

	private void judgeFileName() {
		
		Collection<?> files = FileUtils.listFiles(queue, new String[]{"fq"}, true);
		Assert.assertEquals(1, files.size());
		File file = (File) files.toArray()[0];
		String fileName = file.getName();
		
		if(logger.isInfoEnabled()){
			logger.info("[judgeFileName]" + fileName);
		}
		String index = fileName.substring("fdata-".length(), fileName.indexOf(".fq"));
		BigDecimal    intIndex = new BigDecimal(index);
		if(logger.isInfoEnabled()){
			logger.info("[judgeFileName]" + intIndex);
		}
		Assert.assertTrue(intIndex.longValue() > 0);
		
		
	}

	private Long setFileSize(Long fileSize) throws IllegalArgumentException, IllegalAccessException {
		
		String MSG_AVG_LEN = "DEFAULT_FILEQUEUE_SIZE";
		Long previous = fileSize;
		Class<FileQueueHolder> clazz = FileQueueHolder.class;
		for(Field field : clazz.getDeclaredFields()){
			
			if(field.getName().equals(MSG_AVG_LEN)){
				
				field.setAccessible(true);
				
				if(logger.isInfoEnabled()){
					logger.info("[testTwoFile][" + field+ "]" + field.get(clazz));
				}
				previous = field.getLong(clazz);
				field.set(clazz, fileSize);
				if(logger.isInfoEnabled()){
					logger.info("[testTwoFile][" + field+ "]" + field.get(clazz));
				}
			}
		}
		
		return previous;
	}

	@Test
	public void testAsyncSeparately() throws SendFailedException, RemoteServiceInitFailedException{
		
		mode = ProducerMode.ASYNC_SEPARATELY_MODE;
		
		File retryFile = new File(fileQueueBaseDir, topic + HandlerAsynchroSeparatelyMode.RETRY_QUEUE_SUFFIX);
		Assert.assertFalse(retryFile.exists());
		
		runAsync();
		Assert.assertTrue(retryFile.exists());
	}

	
	private void runAsync() throws SendFailedException, RemoteServiceInitFailedException {
		
		Assert.assertFalse(queue.exists());
		
		Consumer consumer = addListener(topic);
		sendMessage(messageCount, topic, sendMessageSize);
		
		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
		Assert.assertTrue(queue.exists());
		
	}


	@Override
	protected void setProducerConfig(ProducerConfig config) {
		
		config.setMode(mode);
		config.setFilequeueBaseDir(fileQueueBaseDir);
		config.setThreadPoolSize(10);
	}

	

}

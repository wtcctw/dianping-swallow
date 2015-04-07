package com.dianping.swallow.consumerserver.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.InitializingBean;

import com.dianping.swallow.common.internal.pool.DefaultThreadProfile;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;

/**
 * 线程池实现
 * @author mengwenchao
 *
 * 2014年11月5日 下午5:40:34
 */
public class ConsumerThreadPoolManagerImpl implements ConsumerThreadPoolManager, InitializingBean{

	private int cpuNum = Runtime.getRuntime().availableProcessors();
	
	private int coreRetrieverThreadPoolSize = cpuNum*10;
	
	private int maxRetrieverThreadPoolSize = cpuNum*40;
	
	private int coreServiceHandlerThreadPoolSize = cpuNum;
	
	private int maxServiceHandlerThreadPoolSize = cpuNum*2;
	
	private int coreSendMessageThreadPoolSize = cpuNum;
	
	private int maxSendMessageThreadPoolSize = cpuNum*2;

	private ExecutorService serviceHandlerThreadPool;

	private ExecutorService retrieverThreadPool;
	
	private ExecutorService sendMessageThreadPool;
	
	private ScheduledExecutorService scheduled;
	
	public ConsumerThreadPoolManagerImpl(){
	
	}

	@Override
	public ExecutorService getServiceHandlerThreadPool() {

		return serviceHandlerThreadPool;
	}

	@Override
	public ExecutorService getRetrieverThreadPool() {

		return retrieverThreadPool;

	}

	@Override
	public ExecutorService getSendMessageThreadPool() {

		return sendMessageThreadPool;

	}


	public void createPool(){

		DefaultThreadProfile service = new DefaultThreadProfile("CONSUMER_SERVICE");
		service.setCorePoolSize(coreServiceHandlerThreadPoolSize);
		service.setMaxPoolSize(maxServiceHandlerThreadPoolSize);
		serviceHandlerThreadPool = service.createPool();
		
		DefaultThreadProfile retriever = new DefaultThreadProfile("CONSUMER_RETRIVER");
		retriever.setCorePoolSize(coreRetrieverThreadPoolSize);
		retriever.setMaxPoolSize(maxRetrieverThreadPoolSize);
		retrieverThreadPool = retriever.createPool();
		
		DefaultThreadProfile sender = new DefaultThreadProfile("CONSUMER_SENDER");
		sender.setCorePoolSize(coreSendMessageThreadPoolSize);
		sender.setMaxPoolSize(maxSendMessageThreadPoolSize);
		sendMessageThreadPool = sender.createPool();
		
		
		scheduled = Executors.newScheduledThreadPool(cpuNum, new MQThreadFactory("SCHEDULE-"));		
	}



	public int getCpuNum() {
		return cpuNum;
	}

	public void setCpuNum(int cpuNum) {
		this.cpuNum = cpuNum;
	}

	public int getCoreRetrieverThreadPoolSize() {
		return coreRetrieverThreadPoolSize;
	}

	public void setCoreRetrieverThreadPoolSize(int coreRetrieverThreadPoolSize) {
		this.coreRetrieverThreadPoolSize = coreRetrieverThreadPoolSize;
	}

	public int getMaxRetrieverThreadPoolSize() {
		return maxRetrieverThreadPoolSize;
	}

	public void setMaxRetrieverThreadPoolSize(int maxRetrieverThreadPoolSize) {
		this.maxRetrieverThreadPoolSize = maxRetrieverThreadPoolSize;
	}

	public int getCoreServiceHandlerThreadPoolSize() {
		return coreServiceHandlerThreadPoolSize;
	}

	public void setCoreServiceHandlerThreadPoolSize(
			int coreServiceHandlerThreadPoolSize) {
		this.coreServiceHandlerThreadPoolSize = coreServiceHandlerThreadPoolSize;
	}

	public int getMaxServiceHandlerThreadPoolSize() {
		return maxServiceHandlerThreadPoolSize;
	}

	public void setMaxServiceHandlerThreadPoolSize(
			int maxServiceHandlerThreadPoolSize) {
		this.maxServiceHandlerThreadPoolSize = maxServiceHandlerThreadPoolSize;
	}

	public int getCoreSendMessageThreadPoolSize() {
		return coreSendMessageThreadPoolSize;
	}

	public void setCoreSendMessageThreadPoolSize(
			int coreSendMessageThreadPoolSize) {
		this.coreSendMessageThreadPoolSize = coreSendMessageThreadPoolSize;
	}

	public int getMaxSendMessageThreadPoolSize() {
		return maxSendMessageThreadPoolSize;
	}

	public void setMaxSendMessageThreadPoolSize(int maxSendMessageThreadPoolSize) {
		this.maxSendMessageThreadPoolSize = maxSendMessageThreadPoolSize;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		createPool();
	}

	@Override
	@PreDestroy
	public void dispose() throws Exception {
		serviceHandlerThreadPool.shutdownNow();
		sendMessageThreadPool.shutdownNow();
		retrieverThreadPool.shutdownNow();
		scheduled.shutdownNow();
	}

	@Override
	public ScheduledExecutorService getScheduledThreadPool() {
		return scheduled;
	}

}

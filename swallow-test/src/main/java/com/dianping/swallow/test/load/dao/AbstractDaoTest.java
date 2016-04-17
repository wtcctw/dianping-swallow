package com.dianping.swallow.test.load.dao;

import java.util.Date;
import java.util.HashMap;

import com.dianping.swallow.common.internal.config.SwallowServerConfig;
import com.dianping.swallow.common.internal.config.impl.SwallowConfigDistributed;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.DefaultMessageDaoFactory;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.test.AbstractSwallowTest;
import com.dianping.swallow.test.load.AbstractLoadTask;
import com.dianping.swallow.test.load.AbstractLoadTest;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *
 * 2015年3月24日 下午6:26:06
 */
public abstract class AbstractDaoTest extends AbstractLoadTest{

	@JsonIgnore
	protected MessageDAO<?> dao;
	

	@Override
	protected void start() throws Exception {
		
		dao = createDao();
		super.start();
		
	}
	
	protected MessageDAO<?> createDao() throws Exception {
		
		System.setProperty("lion.useLocal", "true");
		
		DefaultMessageDaoFactory factory = new DefaultMessageDaoFactory();
		
		SwallowServerConfig swallowServerConfig = createSwallowServerConfig(); 
		factory.setSwallowServerConfig(swallowServerConfig);
		factory.setClusterManager(AbstractSwallowTest.createClusterManager(swallowServerConfig));
		
		factory.initialize();

		MessageDAO<?> dao = factory.getObject();
		return dao;
	}


	private SwallowServerConfig createSwallowServerConfig() throws Exception {
		
		SwallowServerConfig swallowServerConfig = new SwallowConfigDistributed();
		swallowServerConfig.initialize();
		
		return swallowServerConfig;
	}

	
	
	
	protected SwallowMessage createMessage(String content) {
		
		SwallowMessage message = new SwallowMessage();
		message.setContent(content);
		message.setGeneratedTime(new Date());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("property-key", "property-value");
		message.setProperties(map);
		message.setSha1("sha-1 string");
		message.setVersion("0.6.0");
		message.setType("feed");
		message.setSourceIp("localhost");
		return message;
	}

	
	@Override
	protected Runnable createLoadTask(String topicName, int concurrentIndex) {
		
		return new AbstractLoadTask(topicName, concurrentIndex) {

			@Override
			protected void doRun() {
				
				while(true){
					try{
						saveMessge(topicName);
					}catch(Exception e){
						logger.error("error save message", e);
					}finally{
					}
				
				}
			}
		};
	}
	
	protected void saveMessge(String topicName) {
		
		dao.saveMessage(topicName, createMessage(message));
		increaseAndGetCurrentCount();
	}

}

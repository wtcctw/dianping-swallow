package com.dianping.swallow.test.load.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.LionDynamicConfig;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.test.load.AbstractLoadTest;

/**
 * @author mengwenchao
 *
 * 2015年3月24日 下午6:26:06
 */
public abstract class AbstractMongoTest extends AbstractLoadTest{
	
	protected MessageDAO dao;

	@Override
	protected void start() throws InterruptedException ,IOException {
		dao = createDao();
		super.start();
		
	};
	
	protected MessageDAO createDao() throws IOException {
		
		DefaultMongoManager mc = new DefaultMongoManager("swallow.mongo.producerServerURI", new DynamicConfig() {
			
			private DynamicConfig config = new LionDynamicConfig("swallow-mongo-lion.properties");
			
			@Override
			public String get(String key) {
				
				if(key.equals("swallow.mongo.producerServerURI")){
					try {
						return getTopicToMongo();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return config.get(key);
			}
			@Override
			public void addConfigChangeListener(ConfigChangeListener listener) {
				
			}
		});
		
		MessageDAOImpl mdao = new MessageDAOImpl();
		mdao.setMongoManager(mc);
		return mdao;
	}

	/**
	 * @return
	 * @throws IOException 
	 */
	private String getTopicToMongo() throws IOException {
		
		Properties p = new Properties();
		InputStream ins = getClass().getClassLoader().getResourceAsStream("swallow-mongo-lion.properties");
		if(ins == null){
			throw new IllegalStateException("file not found: swallow-mongo-lion.properties");
		}
		p.load(ins);
		String result = p.getProperty("swallow.mongo.producerServerURI");
		if(StringUtils.isBlank(result)){
			throw new IllegalStateException("swallow.mongo.producerServerURI not found!!!");
		}
		return result;
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

	
	protected void sendMessage(int topicCount, int concurrentCount, final int messageCount) {

		logger.info("[doStart][config]" + topicCount + "," + concurrentCount + "," + messageCount);
		

		for(int i = 0 ;i< topicCount; i++){
			
			for(int j=0;j<concurrentCount;j++){
				
				final String realTopicName = getTopicName(topicName, i);
				executors.execute(new Runnable(){
					@Override
					public void run() {
	
						while(true){
							try{
								if(count.get() > messageCount){
									break;
								}
								dao.saveMessage(realTopicName, createMessage(message));
								count.incrementAndGet();
							}catch(Exception e){
								logger.error("error save message", e);
							}finally{
							}
						}
						
					}
				});
			}
		}
	}

}

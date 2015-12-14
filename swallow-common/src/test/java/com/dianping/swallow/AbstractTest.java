package com.dianping.swallow;

import com.dianping.lion.client.ConfigCache;
import com.dianping.swallow.common.internal.message.InternalProperties;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.TestSkipRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author mengwenchao
 * 
 *         2015年2月4日 下午4:52:52
 */
public abstract class AbstractTest {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private final int localWebPort = 8080;
	
	private String localIp;

	protected ExecutorService executors = Executors.newCachedThreadPool();

	protected ScheduledExecutorService scheduledExecutors = Executors.newScheduledThreadPool(Runtime.getRuntime()
			.availableProcessors());

	private String topicName = "SwallowUnitTest";

	protected String baseConsumerId = "ut";

	@Rule
	public TestName testName = new TestName();

	@Rule
	public TestSkipRule testSkipRule = new TestSkipRule();
	
	private Properties properties;

	@Before
	public void beforeAbstractTest() {
		if (logger.isInfoEnabled()) {
			logger.info("[-----------------][begin test]" + testName.getMethodName());
		}

		System.setProperty("lion.useLocal", "true");
		
		
		//缓存属性信息，after之后恢复
		properties = (Properties) System.getProperties().clone();
		
		
		//清理残存配置信息
		ConfigCache.getInstance().setPts(new Properties());
		
		localIp = IPUtil.getFirstNoLoopbackIP4Address();
		MockitoAnnotations.initMocks(this);

	}

	
	protected String getTopic() {
		return topicName;
	}

	protected void sleep(int miliSeconds) {

		try {
			TimeUnit.MILLISECONDS.sleep(miliSeconds);
		} catch (InterruptedException e) {
			logger.error("[sleep]", e);
		}
	}

	public SwallowMessage createMessage(Long messageId) {
		
		SwallowMessage message = new SwallowMessage();
		message.setMessageId(messageId);
		message.setContent("this is a SwallowMessage");
		message.setGeneratedTime(new Date());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("property-key", "property-value");
		message.setProperties(map);
		message.setSha1("sha-1 string");
		message.setVersion("0.6.0");
		message.setType("feed");
		message.setSourceIp("localhost");

		message.putInternalProperty(InternalProperties.SAVE_TIME, String.valueOf(System.currentTimeMillis() - 50));
		return message;
	}

	public SwallowMessage createMessage() {
		return createMessage(System.currentTimeMillis());
	}

	protected boolean testLocalWebServer() {

		Socket s = null;

		try {
			s = new Socket("127.0.0.1", localWebPort);
		} catch (Exception e) {
			logger.error("[testLocalWebServer]", e);
			return false;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					logger.error("[testLocalWebServer][close]", e);
				}
			}
		}
		return true;
	}

	protected String getConsumerId() {

		return baseConsumerId + "-" + testName.getMethodName();
	}

	@After
	public void afterAbstractTest() {

		if (logger.isInfoEnabled()) {
			logger.info("[-----------------][end test]" + testName.getMethodName());
		}

		System.setProperties(properties);
		executors.shutdownNow();
		scheduledExecutors.shutdownNow();
	}

	

	public boolean equalsWithoutMessageId(SwallowMessage message1, SwallowMessage message2) {
		
		return equals(message1, message2, true, false);
	}

	public boolean equals(SwallowMessage message1, SwallowMessage message2) {

		return equals(message1, message2, false, true);
	}

	public boolean equals(SwallowMessage message1, SwallowMessage message2, boolean compareInternalProperties, boolean includeMessageId) {

		if (message1 == message2) {
			return true;
		}

		if (message1 == null || message2 == null) {
			return false;
		}

		try {
			for (Field field : SwallowMessage.class.getDeclaredFields()) {
				
				field.setAccessible(true);
				Object fieldValue1 = field.get(message1);
				Object fieldValue2 = field.get(message2);
				
				if(!compareInternalProperties && field.getName().equals("internalProperties")){
					continue;
				}
				
				if(!includeMessageId && field.getName().equals("messageId")){
					continue;
				}
				
				if(!(fieldValue1 == null ? fieldValue2 == null : fieldValue1.equals(fieldValue2))){
					if(logger.isInfoEnabled()){
						logger.info("[equals][" + field.getName() + "]:" + fieldValue1 + "," + fieldValue2);
					}
					return false;
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("", e);
		}
		
		return true;
	}
	
	protected String randomString(){
		
		return UUID.randomUUID().toString();
	}

	protected String randomString(int length){

		StringBuilder sb = new StringBuilder();
		for(int i=0;i<length;i++){
			
			int random = (int) (Math.random() * ('a' - 'A'));
			sb.append((char)('A' + random));
		}
		return sb.toString();
	}
	
	public String getLocalIp() {
		return localIp;
	}


	protected Long randomLong() {
		return (long) (Math.random() * Long.MAX_VALUE);
	}
	
	
}

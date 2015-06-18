package com.dianping.swallow.common.internal.config.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.lion.client.ConfigCache;
import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.SwallowConfig.TopicConfig;
import com.dianping.swallow.common.internal.util.EnvUtil;

/**
 * @author mengwenchao
 * 
 *         2015年6月12日 下午3:59:29
 */
public class SwallowConfigImplCentralTest extends AbstractTest {

	private SwallowConfigCentral swallowConfig;

	@Before
	public void beforeSwallowConfigImplCentralTest() throws Exception {

		swallowConfig = new SwallowConfigCentral();
		swallowConfig.initialize();
	}

	@Test
	public void testparseServerURIString() {

		String server1 = "mongodb://127.0.0.1";
		String server2 = "mongodb://127.0.0.2";
		String topic1 = "topic1";
		String topic2 = "topic2";
		String def = "default=mongodb://127.0.0.111;";

		String[] urls = new String[] {
				def + "topic1=" + server1 + ";topic2=" + server2,
				def + "topic1=" + server1 + ";\ntopic2=" + server2,
				def + "topic1=" + server1 + ";\rtopic2=" + server2,
				def + "topic1=" + server1 + ";\n\rtopic2=" + server2,
				def + "topic1 =" + server1 + ";\n\rtopic2 =" + server2,
				def + " topic1 = " + server1 + " ;\n\rtopic2 = " + server2 };

		for (String url : urls) {

			if (logger.isInfoEnabled()) {
				logger.info("[testParse][url]" + url);
			}
			Map<String, String> topicToMongo = swallowConfig.parseServerURIString(url, "");
			Assert.assertEquals(server1, topicToMongo.get(topic1));
			Assert.assertEquals(server2, topicToMongo.get(topic2));
		}
	}
	
	@Test
	public void testTopicMongo(){
		if(EnvUtil.isDev()){
			return;
		}
		
		TopicConfig []configs = new TopicConfig[]{
				new TopicConfig("mongodb://192.168.213.143:27018", 111, 1111),
				new TopicConfig("mongodb://192.168.213.143:27118", 222, 2222)
		}; 
		
		LionUtil lionUtil = new LionUtilImpl(2L);
		ConfigCache cache = ConfigCache.getInstance();
		String rawConfigMongo = cache.getProperty(SwallowConfigCentral.LION_KEY_MONGO_URLS);
		String rawConfigSize = cache.getProperty(SwallowConfigCentral.LION_KEY_MSG_CAPPED_COLLECTION_SIZE);
		String rawConfigMax = cache.getProperty(SwallowConfigCentral.LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM);
		
		try{
			for(TopicConfig config : configs){
				
				String newConfigMongo = rawConfigMongo + ";" + topicName + "=" + config.getMongoUrl();
				String newConfigSize =  rawConfigSize + ";" + topicName + "=" +  config.getSize();
				String newConfigMax =  rawConfigMax + ";" + topicName + "=" + config.getMax();
				lionUtil.createOrSetConfig(SwallowConfigCentral.LION_KEY_MONGO_URLS, newConfigMongo);
				lionUtil.createOrSetConfig(SwallowConfigCentral.LION_KEY_MSG_CAPPED_COLLECTION_SIZE, newConfigSize);
				lionUtil.createOrSetConfig(SwallowConfigCentral.LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM, newConfigMax);
				
				sleep(1000);
				TopicConfig topicConfig = swallowConfig.getTopicConfig(topicName);
				Assert.assertEquals(config.getMongoUrl(), topicConfig.getMongoUrl());
				Assert.assertEquals(config.getMongoUrl(), topicConfig.getMongoUrl());
				Assert.assertEquals(config.getMongoUrl(), topicConfig.getMongoUrl());
			}
		}finally{
			//恢复老配置
			lionUtil.createOrSetConfig(SwallowConfigCentral.LION_KEY_MONGO_URLS, rawConfigMongo);
			lionUtil.createOrSetConfig(SwallowConfigCentral.LION_KEY_MSG_CAPPED_COLLECTION_SIZE, rawConfigSize);
			lionUtil.createOrSetConfig(SwallowConfigCentral.LION_KEY_MSG_CAPPED_COLLECTION_MAX_DOC_NUM, rawConfigMax);
		}
		
	}
	
	@Test
	public void testUrlConnection() throws IOException{
		
		URL url = new URL("http://192.168.218.22:1234");
		
		for(int i=0 ;i<1;i++){
			
			HttpURLConnection connection = null;
			try{
				connection = (HttpURLConnection) url.openConnection();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				String line = null;
				while((line = reader.readLine()) != null){
					System.out.println(line);
				}
			}finally{
				if(connection != null){
					connection.disconnect();
				}
			}
		}
	}
}

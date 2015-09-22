package com.dianping.swallow.web.controller.chain.lion;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.chain.config.Configure;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午10:55:20
 */
public class AbstractLionEditorTest extends MockTest{
	
	@Mock
	private LionUtil lionUtil;
	
	@Mock
	private TopicResourceService topicResourceService;
	
	@Mock
	private ConsumerServerResourceService consumerServerResourceService;
	
	@Mock
	private TopicWhiteList topicWhiteList;
	
	private TopicWhiteListLionEditor lionEditor;
	
	private LionConfigBean lionConfigBean;
	
	private Set<String> topics = new HashSet<String>();
	
	private String key;
	
	private String value;

	@Before
	public void setUp() throws Exception {
		
		TopicCfgLionEditor topicCfgLionEditor = new TopicCfgLionEditor();
		ConsumerServerLionEditor consumerServerLionEditor = new ConsumerServerLionEditor(topicCfgLionEditor);
		TopicWhiteListLionEditor topicWhiteListLionEditor = new TopicWhiteListLionEditor(consumerServerLionEditor);
		
		topicCfgLionEditor.setLionUtil(lionUtil);
		topicCfgLionEditor.setTopicResourceService(topicResourceService);
		
		consumerServerLionEditor.setLionUtil(lionUtil);
		consumerServerLionEditor.setTopicResourceService(topicResourceService);
		consumerServerLionEditor.setConsumerServerResourceService(consumerServerResourceService);
		
		topicWhiteListLionEditor.setLionUtil(lionUtil);
		topicWhiteListLionEditor.setTopicResourceService(topicResourceService);
		topicWhiteListLionEditor.setTopicWhiteList(topicWhiteList);
		lionEditor = topicWhiteListLionEditor;
		
		/*------------------topicWhiteList----------------------*/
		topics = new HashSet<String>();
		topics.add("example");
		topics.add("example2");
		topics.add("example3");
		
		Configure.ConfigureResult configureResult = new Configure.ConfigureResult();
		configureResult.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
		configureResult.setMongoServer("11.22.33.44:8000,55.66.77.88:8001");
		configureResult.setSize4servenday(500);
		configureResult.setResponseStatus(ResponseStatus.SUCCESS);
		
		lionConfigBean = new LionConfigBean();
		lionConfigBean.setTest(Boolean.TRUE);
		lionConfigBean.setTopic("swallow-test");
		lionConfigBean.setConfigureResult(configureResult);
		
		lionEditor = topicWhiteListLionEditor;
		lionEditor.setLionUtil(lionUtil);
		lionEditor.setTopicResourceService(topicResourceService);
		lionEditor.setTopicWhiteList(topicWhiteList);
		Mockito.doReturn(topics).when(topicWhiteList).getTopics();
		
		key = "swallow.topic.whitelist";
		value = StringUtils.join(topics, ";");
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);
		
		/*-----------------consumerServerUri---------------------*/
		String consumerServerConfig = "default=3.3.3.3:8000,4.4.4.4:8001;\nswallow-hao=5.5.5.5:8000,6.6.6.6:8001";
		Mockito.doReturn(consumerServerConfig).when(consumerServerResourceService).loadConsumerServerLionConfig();
		
		key = "swallow.consumer.consumerServerURI";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(consumerServerConfig).append(";\n").append(lionConfigBean.getTopic()).append("=")
				.append(configureResult.getConsumerServer());
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, stringBuilder.toString());
		
		/*-----------------topiccfg--------------------*/
		String topic = lionConfigBean.getTopic();
		key = "swallow.topiccfg." + topic;
		MongoConfigBean mongoConfigBean = new MongoConfigBean();
		String mongoURL = "mongodb://" + lionConfigBean.getConfigureResult().getMongoServer();
		mongoConfigBean.setMongoUrl(mongoURL);
		mongoConfigBean.setSize(lionConfigBean.getConfigureResult().getSize4servenday());

		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		value = jsonBinder.toJson(mongoConfigBean);
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);
		
	}

	@Test
	public void test() {
		Assert.assertTrue(lionEditor.editLion(lionConfigBean) == ResponseStatus.SUCCESS);
	}

}

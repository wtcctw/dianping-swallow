package com.dianping.swallow.web.controller.chain.lion;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.chain.config.Configure;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午9:57:16
 */
public class TopicWhiteListLionEditorTest extends MockTest{
	
	@Mock
	private LionUtil lionUtil;
	
	@Mock
	private TopicResourceService topicResourceService;
	
	@Mock
	private TopicWhiteList topicWhiteList;
	
	private TopicWhiteListLionEditor lionEditor;
	
	private LionConfigBean lionConfigBean;
	
	private Set<String> topics;

	@Before
	public void setUp() throws Exception {
		
		topics = new HashSet<String>();
		topics.add("example");
		topics.add("example2");
		topics.add("example3");
		TopicWhiteListLionEditor topicWhiteListLionEditor = new TopicWhiteListLionEditor();
		
		Configure.ConfigureResult configureResult = new Configure.ConfigureResult();
		configureResult.setConsumerServer("1.1.1.1");
		configureResult.setMongoServer("2.2.2.2");
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
		
		String key = "swallow.topic.whitelist";
		String value = StringUtils.join(topics, ";");
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);
	}

	@Test
	public void test() {
		
		Assert.assertTrue(lionEditor.editLion(lionConfigBean) == ResponseStatus.SUCCESS);
		Assert.assertTrue(topics.size() == 3); //oldTopic
	}

}

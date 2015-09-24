package com.dianping.swallow.web.controller.filter.lion;

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
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.service.TopicResourceService;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午9:57:16
 */
public class TopicWhiteListLionFilterTest extends MockTest{
	
	@Mock
	private LionUtil lionUtil;
	
	@Mock
	private TopicResourceService topicResourceService;
	
	@Mock
	private TopicWhiteList topicWhiteList;
	
	private TopicWhiteListLionFilter lionEditor;
	
	private LionFilterEntity lionFilterEntity;
	
	private Set<String> topics;
	
	private LionFilterChain lionFilterChain = new LionFilterChain();

	@Before
	public void setUp() throws Exception {
		
		topics = new HashSet<String>();
		topics.add("example");
		topics.add("example2");
		topics.add("example3");
		TopicWhiteListLionFilter topicWhiteListLionEditor = new TopicWhiteListLionFilter();
		
		lionFilterEntity = new LionFilterEntity();
		lionFilterEntity.setTest(Boolean.TRUE);
		lionFilterEntity.setTopic("swallow-test");
		
		LionConfigure lionConfigure = new LionConfigure();
		lionConfigure.setConsumerServer("1.1.1.1");
		lionConfigure.setMongoServer("2.2.2.2");
		lionConfigure.setSize4sevenday(500);
		
		lionFilterEntity.setLionConfigure(lionConfigure);
		
		lionEditor = topicWhiteListLionEditor;
		lionEditor.setLionUtil(lionUtil);
		lionEditor.setTopicResourceService(topicResourceService);
		lionEditor.setTopicWhiteList(topicWhiteList);
		Mockito.doReturn(topics).when(topicWhiteList).getTopics();
		
		String key = "swallow.topic.whitelist";
		String value = StringUtils.join(topics, ";");
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);
		
		lionFilterChain.addFilter(lionEditor);
	}

	@Test
	public void test() {
		
		LionFilterResult result = new LionFilterResult();
		lionFilterChain.doFilter(lionFilterEntity, result, lionFilterChain);
		Assert.assertTrue(result.getStatus() == 0);
		Assert.assertTrue(topics.size() == 3); //oldTopic
	}

}


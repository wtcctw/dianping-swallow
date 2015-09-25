package com.dianping.swallow.web.controller.filter.lion;

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
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.TopicResourceService;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午9:57:16
 */
public class LionFilterChainTest extends MockTest{
	
	@Mock
	private LionUtil lionUtil;
	
	@Mock
	private TopicResourceService topicResourceService;
	
	@Mock
	private TopicWhiteList topicWhiteList;
	
	@Mock
	private ConsumerServerResourceService consumerServerResourceService;
	
	private TopicWhiteListLionFilter topicWhiteListLionFilter;
	
	private ConsumerServerLionFilter consumerServerLionFilter;
	
	private TopicCfgLionFilter topicCfgLionFilter;
	
	private LionFilterEntity lionFilterEntity;
	
	private Set<String> topics;
	
	private LionFilterChain lionFilterChain = new LionFilterChain();

	@Before
	public void setUp() throws Exception {
		
		topics = new HashSet<String>();
		topics.add("example");
		topics.add("example2");
		topics.add("example3");
		topicWhiteListLionFilter = new TopicWhiteListLionFilter();
		
		lionFilterEntity = new LionFilterEntity();
		lionFilterEntity.setTest(Boolean.TRUE);
		lionFilterEntity.setTopic("swallow-test");
		
		LionConfigure lionConfigure = new LionConfigure();
		lionConfigure.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
		lionConfigure.setMongoServer("11.22.33.44:8000,55.66.77.88:8001");
		lionConfigure.setSize4sevenday(500);
		
		lionFilterEntity.setLionConfigure(lionConfigure);
		
		lionFilterEntity.setLionConfigure(lionConfigure);
		
		topicWhiteListLionFilter.setLionUtil(lionUtil);
		topicWhiteListLionFilter.setTopicResourceService(topicResourceService);
		topicWhiteListLionFilter.setTopicWhiteList(topicWhiteList);
		Mockito.doReturn(topics).when(topicWhiteList).getTopics();
		
		String key = "swallow.topic.whitelist";
		String value = StringUtils.join(topics, ";");
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);
		
		lionFilterChain.addFilter(topicWhiteListLionFilter);
		
		/*-------------------------------------------------*/
		
		consumerServerLionFilter = new ConsumerServerLionFilter();
		consumerServerLionFilter.setLionUtil(lionUtil);
		consumerServerLionFilter.setTopicResourceService(topicResourceService);
		consumerServerLionFilter.setConsumerServerResourceService(consumerServerResourceService);
		
		String consumerServerConfig = "default=3.3.3.3:8000,4.4.4.4:8001;\nswallow-hao=5.5.5.5:8000,6.6.6.6:8001";
		Mockito.doReturn(consumerServerConfig).when(consumerServerResourceService).loadConsumerServerLionConfig();
		
		key = "swallow.consumer.consumerServerURI";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(consumerServerConfig).append(";\n").append(lionFilterEntity.getTopic()).append("=")
				.append(lionConfigure.getConsumerServer());
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, stringBuilder.toString());
		
		lionFilterChain.addFilter(consumerServerLionFilter);

		/*-------------------------------------------------*/
		
		topicCfgLionFilter = new TopicCfgLionFilter();
		
		topicCfgLionFilter.setLionUtil(lionUtil);
		topicCfgLionFilter.setTopicResourceService(topicResourceService);
		
		String topic = lionFilterEntity.getTopic();
		key = "swallow.topiccfg." + topic;
		MongoConfigBean mongoConfigBean = new MongoConfigBean();
		String mongoURL = "mongodb://" + lionFilterEntity.getLionConfigure().getMongoServer();
		mongoConfigBean.setMongoUrl(mongoURL);
		mongoConfigBean.setSize(lionFilterEntity.getLionConfigure().getSize4sevenday());
		
		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		value = jsonBinder.toJson(mongoConfigBean);
		Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);

		lionFilterChain.addFilter(topicCfgLionFilter);
	}

	@Test
	public void test() {
		
		LionFilterResult result = new LionFilterResult();
		lionFilterChain.doFilter(lionFilterEntity, result, lionFilterChain);
		Assert.assertTrue(result.getStatus() == 0);
	}

}


package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.filter.config.LionConfigManager;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.impl.ConsumerServerResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mingdongli
 *         15/10/23 下午6:16
 */
public class LionHandlerChainTest extends MockTest {

    @Mock
    private TopicWhiteList topicWhiteList;

    @Mock
    private LionUtil lionUtil;

    @Mock
    private TopicResourceService topicResourceService;

    @Mock
    private LionConfigManager lionConfigManager;

    @Mock
    private ConsumerServerResourceServiceImpl consumerServerResourceService;

    private TopicWhiteListLionHandler topicWhiteListLionHandler;

    private ConsumerServerLionHandler consumerServerLionHandler;

    private TopicCfgLionHandler topicCfgLionHandler;

    private LionEditorEntity lionFilterEntity;

    private EmptyObject result;

    private Set<String> topics;

    @SuppressWarnings("unchecked")
	private LionHandlerChain lionHandlerChain = new LionHandlerChain();

    @Before
    public void setUp() throws Exception {

        topics = new HashSet<String>();
        topics.add("example");
        topics.add("example2");
        topics.add("example3");
        topicWhiteListLionHandler = new TopicWhiteListLionHandler();

        lionFilterEntity = new LionEditorEntity();
        lionFilterEntity.setTest(Boolean.TRUE);
        lionFilterEntity.setTopic("swallow-test");

        lionFilterEntity.setConsumerServer("1.2.3.4:8000,5.6.7.8:8001");
        lionFilterEntity.setStorageServer("mongo://11.22.33.44:8000,55.66.77.88:8001");
        lionFilterEntity.setSize4SevenDay(500);

        topicWhiteListLionHandler.setLionUtil(lionUtil);
        topicWhiteListLionHandler.setTopicResourceService(topicResourceService);
        topicWhiteListLionHandler.setTopicWhiteList(topicWhiteList);
        topicWhiteListLionHandler.setLionConfigManager(lionConfigManager);
        Mockito.doReturn(topics).when(topicWhiteList).getTopics();

        String key = "swallow.topic.whitelist";
        String value = StringUtils.join(topics, ";");
        Mockito.doReturn(value).when(lionUtil).getValue(key);
        Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);

        lionHandlerChain.addHandler(topicWhiteListLionHandler);

		/*-------------------------------------------------*/

        consumerServerLionHandler = new ConsumerServerLionHandler();
        consumerServerLionHandler.setLionUtil(lionUtil);
        consumerServerLionHandler.setTopicResourceService(topicResourceService);
        consumerServerLionHandler.setLionConfigManager(lionConfigManager);

        String consumerServerConfig = "default=3.3.3.3:8000,4.4.4.4:8001;\nswallow-hao=5.5.5.5:8000,6.6.6.6:8001";
        Mockito.doReturn(consumerServerConfig).when(consumerServerResourceService).loadConsumerServerLionConfig();

        key = "swallow.consumer.consumerServerURI";
        Mockito.doReturn(consumerServerConfig).when(lionUtil).getValue(key);
        int length = 10;
        Mockito.doReturn(length).when(lionConfigManager).getConsumerServerUriLength();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(consumerServerConfig).append(";\n").append(lionFilterEntity.getTopic()).append("=")
                .append(lionFilterEntity.getConsumerServer());
        Mockito.doNothing().when(lionUtil).createOrSetConfig(key, stringBuilder.toString());

        lionHandlerChain.addHandler(consumerServerLionHandler);

		/*-------------------------------------------------*/

        topicCfgLionHandler = new TopicCfgLionHandler();

        topicCfgLionHandler.setLionUtil(lionUtil);
        topicCfgLionHandler.setTopicResourceService(topicResourceService);

        String topic = lionFilterEntity.getTopic();
        key = "swallow.topiccfg." + topic;
        MongoConfigBean mongoConfigBean = new MongoConfigBean();
        String mongoURL = lionFilterEntity.getStorageServer();
        mongoConfigBean.setMongoUrl(mongoURL);
        mongoConfigBean.setSize(lionFilterEntity.getSize4SevenDay());

        JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
        value = jsonBinder.toJson(mongoConfigBean);
        Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);

        lionHandlerChain.addHandler(topicCfgLionHandler);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test() {

        result = new EmptyObject();
        ResponseStatus status = lionHandlerChain.handle(lionFilterEntity, result);
        Assert.assertTrue(status.getStatus() == 0);
    }
}
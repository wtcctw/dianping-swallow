package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.controller.filter.config.LionConfigManager;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mingdongli
 *         15/10/23 下午7:27
 */
public class TopicWhiteListLionHandlerTest extends MockTest {

    @Mock
    private LionUtil lionUtil;

    @Mock
    private TopicResourceService topicResourceService;

    @Mock
    private TopicWhiteList topicWhiteList;

    @Mock
    private LionConfigManager lionConfigManager;

    private TopicWhiteListLionHandler lionEditor;

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
        TopicWhiteListLionHandler topicWhiteListLionEditor = new TopicWhiteListLionHandler();

        lionFilterEntity = new LionEditorEntity();
        lionFilterEntity.setTest(Boolean.TRUE);
        lionFilterEntity.setTopic("swallow-test");

        lionFilterEntity.setConsumerServer("1.1.1.1");
        lionFilterEntity.setMongoServer("2.2.2.2");
        lionFilterEntity.setSize4SevenDay(500);


        lionEditor = topicWhiteListLionEditor;
        lionEditor.setLionUtil(lionUtil);
        lionEditor.setTopicResourceService(topicResourceService);
        lionEditor.setTopicWhiteList(topicWhiteList);
        lionEditor.setLionConfigManager(lionConfigManager);
        Mockito.doReturn(topics).when(topicWhiteList).getTopics();

        String key = "swallow.topic.whitelist";
        String value = StringUtils.join(topics, ";");
        Mockito.doReturn(value).when(lionUtil).getValue(key);
        Mockito.doNothing().when(lionUtil).createOrSetConfig(key, value);

        lionHandlerChain.addHandler(lionEditor);

    }

    @Test
    public void test() {

        result = new EmptyObject();
        ResponseStatus status = lionHandlerChain.handle(lionFilterEntity, result);
        Assert.assertTrue(status.getStatus() == 0);
    }
}
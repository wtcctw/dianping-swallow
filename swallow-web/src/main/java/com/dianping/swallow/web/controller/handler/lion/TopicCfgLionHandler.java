package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.model.dom.KafkaConfigBean;
import com.dianping.swallow.web.model.dom.MongoConfigBean;
import com.dianping.swallow.web.util.ResponseStatus;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @author mingdongli
 *         15/10/23 下午4:59
 */
@Component
public class TopicCfgLionHandler extends AbstractLionHandler {

    private static final String PRE_TOPIC_KEY = "swallow.topiccfg.";

    @Override
    protected ResponseStatus doHandlerHelper(LionEditorEntity lionEditorEntity, EmptyObject result) {

        String topic = lionEditorEntity.getTopic();
        boolean isTest = lionEditorEntity.isTest();
        String topicType = lionEditorEntity.getTopicType();

        topicResourceService.loadCachedTopicToAdministrator().put(topic, new HashSet<String>());
        String key = PRE_TOPIC_KEY + topic;
        String storeServerUrl = lionEditorEntity.getStorageServer();
        JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
        String value;

        if (lionEditorEntity.isKafkaType()) {
            KafkaConfigBean kafkaConfigBean = new KafkaConfigBean();
            kafkaConfigBean.setStoreUrl(storeServerUrl);
            kafkaConfigBean.setTopicType(topicType);
            value = jsonBinder.toJson(kafkaConfigBean);
        } else {
            MongoConfigBean mongoConfigBean = new MongoConfigBean();
            mongoConfigBean.setMongoUrl(storeServerUrl);
            mongoConfigBean.setSize(lionEditorEntity.getSize4SevenDay());
            value = jsonBinder.toJson(mongoConfigBean);

        }

        return doEditLion(key, value, "", isTest, null);
    }
}

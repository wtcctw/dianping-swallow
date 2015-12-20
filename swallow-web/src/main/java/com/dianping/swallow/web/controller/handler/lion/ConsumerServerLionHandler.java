package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.web.controller.filter.config.LionConfigManager;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mingdongli
 *         15/10/23 下午4:54
 */
@Component
public class ConsumerServerLionHandler extends AbstractLionHandler{

    public static final String DEFAULT = "default";

    @Autowired
    private LionConfigManager lionConfigManager;

    @Override
    protected synchronized ResponseStatus doHandlerHelper(LionEditorEntity lionEditorEntity, EmptyObject result) {

        String topic = lionEditorEntity.getTopic();
        boolean isTest = lionEditorEntity.isTest();
        String consumerServer = lionEditorEntity.getConsumerServer();
        StringBuilder stringBuilder = new StringBuilder();

        String consumerServerConfig = (String) getValue(TopicResourceServiceImpl.SWALLOW_CONSUMER_SERVER_URI, Boolean.FALSE);
        if(consumerServerConfig != null && consumerServerConfig.endsWith(";")){
            consumerServerConfig = consumerServerConfig.substring(0, consumerServerConfig.length() - 1);
        }
        String defaultConfig = loadDefaultConfig(consumerServerConfig);
        if (StringUtils.isBlank(defaultConfig)) {
            return ResponseStatus.NODEFAULT;
        }
        if (defaultConfig.equals(consumerServer)) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Set swallow.consumer.consumerServerURI of %s to default=%s", topic,
                        defaultConfig));
            }
            return ResponseStatus.SUCCESS;
        }
        stringBuilder.append(consumerServerConfig).append(";\n").append(topic).append("=")
                .append(consumerServer);
        String newConsumerServerLionConfig = stringBuilder.toString();
        if (newConsumerServerLionConfig.length() < lionConfigManager.getConsumerServerUriLength()) {
            return ResponseStatus.INVALIDLENGTH;
        }
        ResponseStatus status = doEditLion(TopicResourceServiceImpl.SWALLOW_CONSUMER_SERVER_URI,
                newConsumerServerLionConfig, consumerServerConfig, isTest, null);
        return status;
    }

    private String loadDefaultConfig(String config) {
        Map<String, String> map = parseServerURIString(config);
        if (map != null) {
            boolean isContained = map.keySet().contains(DEFAULT);
            if (isContained) {
                return map.get(DEFAULT);
            }
        }
        return StringUtils.EMPTY;
    }

    public static Map<String, String> parseServerURIString(String value) {

        Map<String, String> result = new HashMap<String, String>();

        for (String topicNamesToURI : value.split("\\s*;\\s*")) {

            if (StringUtils.isEmpty(topicNamesToURI)) {
                continue;
            }

            String[] splits = topicNamesToURI.split("=");
            if (splits.length != 2) {
                continue;
            }
            String consumerServerURI = splits[1].trim();
            String topicNameStr = splits[0].trim();
            result.put(topicNameStr, consumerServerURI);
        }

        return result;
    }


    public void setLionConfigManager(LionConfigManager lionConfigManager){
        this.lionConfigManager = lionConfigManager;
    }
}

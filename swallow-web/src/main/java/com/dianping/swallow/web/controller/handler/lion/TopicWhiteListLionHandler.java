package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.controller.filter.config.LionConfigManager;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author mingdongli
 *         15/10/23 下午4:27
 */
@Component
public class TopicWhiteListLionHandler extends AbstractLionHandler {

    @Autowired
    private TopicWhiteList topicWhiteList;

    @Autowired
    private LionConfigManager lionConfigManager;

    @Override
    protected synchronized ResponseStatus doHandlerHelper(LionEditorEntity lionEditorEntity, EmptyObject result) {

        String topic = lionEditorEntity.getTopic();
        boolean isTest = lionEditorEntity.isTest();

        Set<String> newTopics = (Set<String>)getValue(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, Boolean.TRUE);
        Set<String> oldTopics = topicWhiteList.getTopics();
        if(newTopics == null || oldTopics == null || (oldTopics.size() - newTopics.size()) > TopicWhiteList.MAX_TOPIC_WHILTE_LIST_DECREASE){
            return ResponseStatus.INVALIDLENGTH;
        }

        Set<String> newTopicsCopy = new LinkedHashSet<String>(newTopics);
        newTopicsCopy.add(topic);
        String topicJoin = StringUtils.join(newTopicsCopy, ";");
        if (topicJoin.length() < lionConfigManager.getWhitelistLength()) {
            return ResponseStatus.INVALIDLENGTH;
        }

        ResponseStatus status = null;

        if(EnvUtil.isProduct()){
            Set<String> envs = EnvUtil.allEnv();
            for(String env : envs){
                status	=  doEditLion(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, topicJoin,
                        StringUtils.join(newTopics, ";"), isTest, env);
                if(status != ResponseStatus.SUCCESS){
                    return ResponseStatus.LIONEXCEPTION;
                }
            }
        }else{
            status	=  doEditLion(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, topicJoin,
                    StringUtils.join(newTopics, ";"), isTest, null);

        }

        return status;
    }

    public void setTopicWhiteList(TopicWhiteList topicWhiteList) {
        this.topicWhiteList = topicWhiteList;
    }

    public void setLionConfigManager(LionConfigManager lionConfigManager) {
        this.lionConfigManager = lionConfigManager;
    }

}

package com.dianping.swallow.common.internal.config;

/**
 * @author qi.yin
 *         2016/01/22  下午2:58.
 */
public interface SwallowClientConfig {

    TopicConfig getTopicCfg(String topic);

    GroupConfig getGroupCfg(String group);

    TopicConfig defaultTopicCfg();

    GroupConfig defaultGroupCfg();

}

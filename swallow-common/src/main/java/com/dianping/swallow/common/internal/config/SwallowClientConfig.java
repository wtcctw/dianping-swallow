package com.dianping.swallow.common.internal.config;

/**
 * @author qi.yin
 *         2016/01/22  下午2:58.
 */
public interface SwallowClientConfig {

    TopicConfig getTopicConfig(String topic);

    GroupConfig getGroupConfig(String group);

    TopicConfig defaultTopicConfig();

}

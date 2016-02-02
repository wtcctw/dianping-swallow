package com.dianping.swallow.common.internal.config.impl;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.*;
import com.dianping.swallow.common.internal.util.StringUtils;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2016/01/25  上午9:43.
 */
public class SwallowClientConfigImpl extends AbstractSwallowConfig implements SwallowClientConfig {

    private static final String KEY_SPLIT = ".";

    public static final int CHECK_CONFIG_INTERVAL = 60;//SECONDS

    private Map<String, TopicConfig> topicCfgs = new ConcurrentHashMap<String, TopicConfig>();

    private Set<String> allTopics = new HashSet<String>();

    private Map<String, GroupConfig> groupCfgs = new ConcurrentHashMap<String, GroupConfig>();

    private Set<String> allGroups = new HashSet<String>();


    private Thread checkConfig;

    public SwallowClientConfigImpl() {

        checkConfig = new Thread(new CheckConfigTask());
        checkConfig.setDaemon(true);
        checkConfig.setName("SwallowClientConfigImpl-checkConfig");
        checkConfig.start();
    }

    @Override
    public TopicConfig getTopicConfig(String topic) {
        return getConfig(allTopics, topicCfgs, TOPIC_CFG_PREFIX, topic, TopicConfig.class);
    }

    @Override
    public GroupConfig getGroupConfig(String group) {

        return getConfig(allGroups, groupCfgs, GROUP_CFG_PREFIX, group, GroupConfig.class);
    }

    @Override
    public TopicConfig defaultTopicConfig() {
        return getTopicConfig(TOPICNAME_DEFAULT);
    }

    @Override
    public void onConfigChange(String key, String value) throws Exception {

        if (!StringUtils.isEmpty(key)) {

            if (key.startsWith(TOPIC_CFG_PREFIX)) {

                replaceConfig(allTopics, topicCfgs, TOPIC_CFG_PREFIX, key, value, TopicConfig.class);

            } else if (key.startsWith(GROUP_CFG_PREFIX)) {

                replaceConfig(allGroups, groupCfgs, GROUP_CFG_PREFIX, key, value, GroupConfig.class);

            }

        }
    }

    private <T> T getConfig(Set<String> configNames, Map<String, T> configs, String configPrefix, String configName, Class<T> clazz) {

        if (!configNames.contains(configName)) {

            String strConfig = dynamicConfig.get(getConfigKey(configPrefix, configName));

            putConfig(configNames, configs, configName, strConfig, clazz);

            synchronized (configNames) {
                configNames.add(configName);
            }
        }

        return configs.get(configName);
    }

    private <T> void putConfig(Set<String> configNames, Map<String, T> configs, String configName, String strConfig, Class<T> clazz) {
        if (!StringUtils.isEmpty(strConfig)) {
            try {
                T newConfig = JsonBinder.getNonEmptyBinder().fromJson(strConfig, clazz);
                putConfig0(configs, configName, newConfig);

            } catch (Exception e) {
                logger.error("[putConfig]config " + strConfig + " deserialized failed. " + e);

                if (!configNames.contains(configName)) {
                    throw new IllegalArgumentException("config " + strConfig + " deserialized exception.", e);
                }
            }
        } else {

            if (configs.containsKey(configName)) {
                configs.remove(configName);
            }
        }

    }

    private <T> void putConfig0(Map<String, T> configs, String configName, T newConfig) {
        T oldConfig = configs.get(configName);

        if (newConfig != oldConfig && newConfig != null && !newConfig.equals(oldConfig)) {
            configs.put(configName, newConfig);

            if (logger.isInfoEnabled()) {
                logger.info("[putConfig0][config]" + configName + ", newCfg = " + newConfig, "oldCfg = " + oldConfig);
            }
        }
    }

    private <T> void replaceConfig(Set<String> configNames, Map<String, T> configs, String configPrefix, String configKey, String strConfig, Class<T> clazz) {

        String configName = configKey.substring(configPrefix.length() + 1);

        if (!StringUtils.isEmpty(configName)) {

            if (configNames.contains(configName)) {
                putConfig(configNames, configs, configName, strConfig, clazz);
            }
        }

    }

    private String getConfigKey(String configPrefix, String configName) {
        return StringUtils.join(KEY_SPLIT, configPrefix, configName);
    }

    class CheckConfigTask implements Runnable {

        private <T> void checkConfigs(Set<String> configNames, Map<String, T> configs, String configPrefix, Class<T> clazz) {

            Iterator<String> iterator = configNames.iterator();

            while (iterator.hasNext()) {
                try {

                    String configName = iterator.next();

                    String strConfig = dynamicConfig.get(getConfigKey(configPrefix, configName));

                    putConfig(configNames, configs, configName, strConfig, clazz);

                } catch (Exception e) {
                    logger.error("[checkConfigs]", e);
                }
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    TimeUnit.SECONDS.sleep(CHECK_CONFIG_INTERVAL);

                    checkConfigs(allTopics, topicCfgs, TOPIC_CFG_PREFIX, TopicConfig.class);

                    checkConfigs(allGroups, groupCfgs, GROUP_CFG_PREFIX, GroupConfig.class);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    logger.error("[run]", e);

                } catch (Throwable t) {

                    logger.error("run", t);

                }
            }
        }

    }

    @Override
    public Set<String> getCfgTopics() {
        return allTopics;
    }

}

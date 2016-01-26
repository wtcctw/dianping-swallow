package com.dianping.swallow.common.internal.config.impl;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.config.*;
import com.dianping.swallow.common.internal.util.PropertiesUtils;
import com.dianping.swallow.common.internal.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2016/01/25  上午9:43.
 */
public class SwallowClientConfigImpl implements SwallowClientConfig, ConfigChangeListener {

    private static final Logger logger = LogManager.getLogger(SwallowClientConfigImpl.class);

    public static final String TOPIC_CFG_PREFIX = "swallow.topiccfg.";

    public static final String GROUP_CFG_PREFIX = "swallow.groupcfg.";

    private static final String DEFAULT_GROUP_NAME = "default";

    private static final String DEFAULT_TOPIC_NAME = "default";

    private final String LION_CONFIG_FILENAME = PropertiesUtils.getProperty("SWALLOW.STORE.LION.CONFFILE", "swallow-store-lion.properties");

    public static final int CHECK_CONFIG_INTERVAL = 60;//SECONDS

    private Map<String, TopicConfig> topicCfgs = new ConcurrentHashMap<String, TopicConfig>();

    private Map<String, GroupConfig> groupCfgs = new ConcurrentHashMap<String, GroupConfig>();

    private DynamicConfig dynamicConfig;

    private Thread checkConfig;

    public SwallowClientConfigImpl() {

        dynamicConfig = new DefaultDynamicConfig(LION_CONFIG_FILENAME);

        dynamicConfig.addConfigChangeListener(this);

        checkConfig = new Thread(new CheckConfigTask());
        checkConfig.setDaemon(true);
        checkConfig.setName("SwallowClientConfigImpl-checkConfig");
        checkConfig.start();
    }

    @Override
    public TopicConfig getTopicCfg(String topic) {
        return getCfgs(topicCfgs, TOPIC_CFG_PREFIX, topic, TopicConfig.class);
    }

    @Override
    public GroupConfig getGroupCfg(String group) {
        return getCfgs(groupCfgs, GROUP_CFG_PREFIX, group, GroupConfig.class);
    }

    @Override
    public TopicConfig defaultTopicCfg() {
        return getTopicCfg(DEFAULT_TOPIC_NAME);
    }

    @Override
    public GroupConfig defaultGroupCfg() {
        return getGroupCfg(DEFAULT_GROUP_NAME);
    }

    @Override
    public void onConfigChange(String key, String value) throws Exception {

        if (!StringUtils.isEmpty(key)) {

            if (key.startsWith(TOPIC_CFG_PREFIX)) {

                replaceCfg(topicCfgs, TOPIC_CFG_PREFIX, key, value, TopicConfig.class);

            } else if (key.startsWith(GROUP_CFG_PREFIX)) {

                replaceCfg(groupCfgs, GROUP_CFG_PREFIX, key, value, GroupConfig.class);

            }

        }
    }

    private <T> T getCfgs(Map<String, T> cfgs, String cfgPrefix, String cfgName, Class<T> clazz) {

        if (!cfgs.containsKey(cfgName)) {

            String cfgKey = cfgPrefix + cfgName;
            String strCfg = dynamicConfig.get(cfgKey);

            putCfg(cfgs, cfgName, strCfg, clazz);
        }
        return cfgs.get(cfgName);
    }

    private <T> void putCfg(Map<String, T> cfgs, String cfgName, String strCfg, Class<T> clazz) {
        if (!StringUtils.isEmpty(strCfg)) {
            try {
                T newCfg = JsonBinder.getNonEmptyBinder().fromJson(strCfg, clazz);
                putCfg0(cfgs, cfgName, newCfg);

            } catch (Exception e) {
                logger.error("[putCfg][config]" + strCfg + " deserialized failed. " + e);

                if (!cfgs.containsKey(cfgName)) {
                    T newCfg = getCfgInstance(clazz);
                    putCfg0(cfgs, cfgName, newCfg);
                }

            }
        } else {

            T newCfg = getCfgInstance(clazz);
            putCfg0(cfgs, cfgName, newCfg);
        }

    }


    private <T> void putCfg0(Map<String, T> cfgs, String cfgName, T newCfg) {
        T oldCfg = cfgs.get(cfgName);

        if (newCfg != oldCfg && newCfg != null && !newCfg.equals(oldCfg)) {
            cfgs.put(cfgName, newCfg);

            if (logger.isInfoEnabled()) {
                logger.info("[putCfg][config]" + cfgName + ", newCfg = " + newCfg, "oldCfg = " + oldCfg);
            }
        }
    }

    private <T> void replaceCfg(Map<String, T> cfgs, String cfgPrefix, String cfgKey, String strCfg, Class<T> clazz) {

        String cfgName = cfgKey.substring(cfgPrefix.length());

        if (cfgs.containsKey(cfgName)) {
            putCfg(cfgs, cfgName, strCfg, clazz);
        }

    }

    public <T> T getCfgInstance(Class<T> clazz) {
        try {

            return (T) clazz.newInstance();

        } catch (Exception e) {

            logger.error("[getCfgInstance] create instance " + clazz.getName() + " failed.", e);
        }

        return null;
    }

    class CheckConfigTask implements Runnable {

        private <T> void checkCfgs(Map<String, T> cfgs, String cfgPrefix, Class<T> clazz) {

            Set<String> keys = cfgs.keySet();
            Iterator<String> iterator = keys.iterator();

            while (iterator.hasNext()) {
                try {

                    String key = iterator.next();
                    String cfgKey = cfgPrefix + key;
                    String strCfg = dynamicConfig.get(cfgKey);

                    putCfg(cfgs, key, strCfg, clazz);

                } catch (Exception e) {
                    logger.error("[checkCfgs]", e);
                }
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    TimeUnit.SECONDS.sleep(CHECK_CONFIG_INTERVAL);

                    checkCfgs(topicCfgs, TOPIC_CFG_PREFIX, TopicConfig.class);

                    checkCfgs(groupCfgs, GROUP_CFG_PREFIX, GroupConfig.class);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    logger.error("[run]", e);

                } catch (Throwable t) {

                    logger.error("run", t);

                }
            }
        }

    }

}

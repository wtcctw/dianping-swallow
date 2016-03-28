package com.dianping.swallow.kafka.admin;

import kafka.admin.AdminUtils;
import org.I0Itec.zkclient.ZkClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * Author   mingdongli
 * 16/3/7  上午11:04.
 */
public class AdminUtil {

    private static final Logger logger = LogManager.getLogger(AdminUtil.class);

    public static boolean createTopic(ZkClient zkClient, String topic, int partitions, int replicationFactor) {
        try {
            AdminUtils.createTopic(zkClient, topic, partitions, replicationFactor, new Properties());
            return true;
        } catch (Exception e) {
            logger.error("[createTopic] create topic error", e);
            return false;
        }
    }

    public static boolean topicExists(ZkClient zkClient, String topic) {
        return AdminUtils.topicExists(zkClient, topic);
    }
}

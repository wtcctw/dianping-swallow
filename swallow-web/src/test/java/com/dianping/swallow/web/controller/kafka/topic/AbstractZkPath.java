package com.dianping.swallow.web.controller.kafka.topic;

import com.dianping.swallow.web.monitor.zookeeper.CuratorConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.junit.Before;

import java.util.List;

/**
 * Author   mingdongli
 * 16/3/1  下午11:34.
 */
public class AbstractZkPath {

    protected CuratorFramework curator;

    protected String zkServer = "192.168.226.41:2181";

    protected static final int N_PARTITION = 3;

    protected static final int N_REPLICATION = 3;

    protected static final String TOPIC = "testcase";


    private CuratorFramework getCurator(CuratorConfig config) {
        CuratorFramework curator = CuratorFrameworkFactory.newClient(config.getZkConnect(),
                new BoundedExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxSleepTimeMs(), config.getZkMaxRetry()));
        curator.start();
        return curator;
    }

    private CuratorFramework getCurator(String zkConnect) {
        CuratorConfig curatorConfig = new CuratorConfig(zkConnect);
        return getCurator(curatorConfig);
    }

    @Before
    public void setUp() throws Exception {
        curator = getCurator(zkServer);
    }

    protected void doDelZnodeRecursively(String path) {
        try {
            List<String> childList = curator.getChildren().forPath(path);
            if (CollectionUtils.isEmpty(childList)) {
                curator.delete().forPath(path);
            } else {
                for(String childName: childList) {
                    String childPath = path + "/" + childName;
                    List<String> grandChildList = curator.getChildren().forPath(childPath);
                    if (CollectionUtils.isEmpty(grandChildList)) {
                        curator.delete().forPath(childPath);
                    } else {
                        doDelZnodeRecursively(childPath);
                    }
                }
                curator.delete().forPath(path);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete node recursively", e);
        }
    }
}

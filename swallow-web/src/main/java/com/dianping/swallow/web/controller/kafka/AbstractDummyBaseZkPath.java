package com.dianping.swallow.web.controller.kafka;

import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import com.dianping.swallow.web.monitor.zookeeper.AbstractBaseZkPath;
import com.dianping.swallow.web.monitor.zookeeper.BaseZkPath;
import com.yammer.metrics.core.MetricName;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * Author   mingdongli
 * 16/3/1  下午4:29.
 */
public abstract class AbstractDummyBaseZkPath extends AbstractBaseZkPath implements BaseZkPath {

    private static final int VERSION = -1;

    @Override
    protected int getInterval() {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    protected int getDelay() {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    protected KafkaEvent createEvent() {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    public Object getMBeanValue(String host, int port, MetricName metricName, Class<?> clazz) throws IOException {
        throw new UnsupportedOperationException("not support");
    }

    protected void updatePersistentPath(CuratorFramework curator, String path, String value) throws Exception {

        byte[] ba = value.getBytes();
        try {
            curator.setData().withVersion(VERSION).forPath(path, ba);
        } catch (KeeperException.NoNodeException e) {
            try {
                curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, ba);
            } catch (KeeperException.NodeExistsException e1) {
                curator.setData().forPath(path, ba);
            } catch (Exception e2) {
                throw e2;
            }
        } catch (Exception e3) {
            throw e3;
        }

    }
}

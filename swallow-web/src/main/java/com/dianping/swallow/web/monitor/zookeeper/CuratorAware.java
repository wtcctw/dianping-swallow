package com.dianping.swallow.web.monitor.zookeeper;

import org.apache.curator.framework.CuratorFramework;

/**
 * Author   mingdongli
 * 16/2/22  下午9:59.
 */
public interface CuratorAware extends BaseZkPath{

    CuratorFramework getCurator(CuratorConfig config);

    CuratorFramework getCurator(String zkConnect);
}

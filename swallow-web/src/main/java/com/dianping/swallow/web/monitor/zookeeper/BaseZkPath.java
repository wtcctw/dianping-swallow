package com.dianping.swallow.web.monitor.zookeeper;

/**
 * Author   mingdongli
 * 16/2/22  下午10:00.
 */
public interface BaseZkPath {

    String zkPath(String path);

    String zkPathFrom(String parent,String child);
}

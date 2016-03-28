package com.dianping.swallow.web.controller.kafka.broker;

import org.apache.curator.framework.CuratorFramework;

import java.util.List;

/**
 * Author   mingdongli
 * 16/3/1  下午5:10.
 */
public interface BrokerDescription {

    List<Integer> getSortedBrokers(CuratorFramework curator) throws Exception;
}

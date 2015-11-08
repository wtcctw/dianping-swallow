package com.dianping.swallow.kafka.zk.impl;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import kafka.utils.ZkUtils;

import com.dianping.swallow.kafka.zk.KafkaZk;

/**
 * @author mengwenchao
 * 
 *         2015年11月6日 下午6:31:15
 */
public class AbstractKafkaZk implements KafkaZk, Watcher {

	protected String ConsumersPath = ZkUtils.ConsumersPath();
	protected String BrokerIdsPath = ZkUtils.BrokerIdsPath();
	protected String BrokerTopicsPath = ZkUtils.BrokerTopicsPath();
	protected String TopicConfigPath = ZkUtils.TopicConfigPath();
	protected String TopicConfigChangesPath = ZkUtils.TopicConfigChangesPath();
	protected String ControllerPath = ZkUtils.ControllerPath();
	protected String ControllerEpochPath = ZkUtils.ControllerEpochPath();
	protected String ReassignPartitionsPath = ZkUtils.ReassignPartitionsPath();
	protected String DeleteTopicsPath = ZkUtils.DeleteTopicsPath();
	protected String PreferredReplicaLeaderElectionPath = ZkUtils.PreferredReplicaLeaderElectionPath();

	protected ZooKeeper zooKeeper;
	
	public static final int DEFAULT_SESSION_TIMEOUT = 5000;

	public AbstractKafkaZk(String zkConnectionString){
		this(zkConnectionString, DEFAULT_SESSION_TIMEOUT);
	}

	public AbstractKafkaZk(String zkConnectionString, int sessionTimeout){
		
		try {
			this.zooKeeper = new ZooKeeper(zkConnectionString, sessionTimeout, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		
	}
	

}

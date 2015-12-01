package com.dianping.swallow.kafka.zookeeper;


import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import kafka.cluster.Broker;
import kafka.utils.ZKStringSerializer$;

import org.I0Itec.zkclient.ZkClient;

import com.dianping.swallow.kafka.TopicAndPartition;

import scala.collection.JavaConversions;
import scala.collection.Seq;

/**
 * 对scaka版本的ZkUtils封装
 * @author mengwenchao
 *
 * 2015年11月6日 下午6:16:00
 */
public class ZkUtils {
	
	public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
	public static final int DEFAULT_SESSION_TIMEOUT = 5000;
	
	private ZkClient zkClient;
	
	public static String BACKUP_PATH = "/swallow_backup";
	
	public ZkUtils(String zkConnectionString){
		this(zkConnectionString, DEFAULT_SESSION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
	}
	
	public ZkUtils(String zkConnectionString, int sessionTimeout, int connectionTimeout){
	
		this.zkClient = new ZkClient(zkConnectionString, sessionTimeout, connectionTimeout, ZKStringSerializer$.MODULE$);
	}
	
	public ZkUtils(List<InetSocketAddress> seeds) {
		this(getAddress(seeds));
	}

	private static String getAddress(List<InetSocketAddress> seeds) {
		
		return null;
	}

	public List<InetSocketAddress>  getAllBrokersInCluster(){

		List<InetSocketAddress> allAddress = new LinkedList<InetSocketAddress>();
		
		Seq<Broker> brokers = kafka.utils.ZkUtils.getAllBrokersInCluster(zkClient);

		for(Broker broker : JavaConversions.asJavaList(brokers)){
			InetSocketAddress address = new InetSocketAddress(broker.host(), broker.port());
			allAddress.add(address);
		}
		
		return allAddress;
	}
	
	public void saveBackupAck(TopicAndPartition tp, String groupId, Long ack){
		
		String path = getPath(tp, groupId);
		zkClient.createPersistent(path, true);
		zkClient.writeData(path, String.valueOf(ack));
		
	}

	private String getPath(TopicAndPartition tp, String groupId) {
		
		return BACKUP_PATH + "/" + groupId + "/" + tp.getTopic() + "/" + tp.getPartition();
	}

	public Long getBackupAck(TopicAndPartition tp, String groupId){
		
		String value = zkClient.readData(getPath(tp, groupId), true);
		if(value == null){
			return null;
		}
		return Long.parseLong(value);
	}
	
	
	public static void main(String []argc){

		ZkUtils zkUtils = new ZkUtils("192.168.104.13:2181");

		long start = System.currentTimeMillis();
		long count = 1000;
		
		for(int i=0;i<count;i++){
			zkUtils.getAllBrokersInCluster();
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println((end - start)/count);
	}

}

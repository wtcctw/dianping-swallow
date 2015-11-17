package com.dianping.swallow.kafka;

import java.net.InetSocketAddress;

import kafka.cluster.Broker;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午6:00:20
 */
public class KafkaBroker {
	
	private InetSocketAddress address;
	

	private int nodeId;
	
	public KafkaBroker(String host, int port, int nodeId){
		this.address = new InetSocketAddress(host, port);
		this.nodeId = nodeId;
	}

	
	public static KafkaBroker fromKafka(Broker broker){
		return new KafkaBroker(broker.host(), broker.port(), broker.id());
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}


}

package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:42:18
 */
public interface IPCollectorService {

	/**
	 * add all ip
	 * 
	 * @param monitorData
	 */
	void addStatisIps(MonitorData monitorData);

	/**
	 * get statis consumerServer ip
	 * 
	 * @param monitorData
	 */
	Map<String, Long> getStatisConsumerServerIps();

	/**
	 * get statis producerServer ip
	 * 
	 * @param monitorData
	 */
	Map<String, Long> getStatisProducerServerIps();

	/**
	 * get producer server ips
	 * 
	 * @return
	 */
	List<String> getProducerServerIps();

	/**
	 * get consumer server slave ips
	 * 
	 * @return
	 */
	List<String> getConsumerServerSlaveIps();

	/**
	 * get consumer server master ips
	 * 
	 * @return
	 */
	List<String> getConsumerServerMasterIps();

	/**
	 * 
	 * @return
	 */
	int getConsumerMasterPort();

	/**
	 * 
	 * @return
	 */
	int getConsumerSlavePort();

	/**
	 * get producer server ips
	 * 
	 * @return
	 */
	List<ProducerServer> getProducerServers();

	/**
	 * get consumer server pair ips
	 * 
	 * @return
	 */
	List<ConsumerServerPair> getConsumerServerPairs();

	/**
	 * get producer server master ips map
	 * 
	 * @return
	 */
	Map<String, String> getProducerServerIpsMap();

	/**
	 * get consumer server master ips map
	 * 
	 * @return
	 */
	Map<String, String> getConsumerServerMasterIpsMap();

	/**
	 * get consumer server slave ips map
	 * 
	 * @return
	 */
	Map<String, String> getConsumerServerSlaveIpsMap();


	public static class ConsumerServerPair {

		public ConsumerServerPair() {

		}

		private ConsumerServer masterServer;

		private ConsumerServer slaveServer;

		public ConsumerServerPair(ConsumerServer masterServer, ConsumerServer slaveServer) {
			this.masterServer = masterServer;
			this.slaveServer = slaveServer;
		}

		public ConsumerServer getMasterServer() {
			return masterServer;
		}

		public void setMasterServer(ConsumerServer masterServer) {
			this.masterServer = masterServer;
		}

		public ConsumerServer getSlaveServer() {
			return slaveServer;
		}

		public void setSlaveServer(ConsumerServer slaveServer) {
			this.slaveServer = slaveServer;
		}

		public boolean equalsMasterIp(String masterIp) {
			return this.masterServer.equalsIp(masterIp);
		}

		public boolean equalsSlaveIp(String slaveIp) {
			return this.slaveServer.equalsIp(slaveIp);
		}

		public boolean equalsMasterServer(ConsumerServer consumerServer) {
			if (this.masterServer.equals(consumerServer)) {
				return true;
			}
			return false;
		}

		public boolean equalsSlaveServer(ConsumerServer consumerServer) {
			if (this.slaveServer.equals(consumerServer)) {
				return true;
			}
			return false;
		}

	}

	public static class ConsumerServer {

		private String ip;

		private String hostName;

		private int port;

		public ConsumerServer() {

		}

		public ConsumerServer(String ip, String hostName, int port) {
			this.ip = ip;
			this.hostName = hostName;
			this.port = port;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ip == null) ? 0 : ip.hashCode());
			result = prime * result + port;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConsumerServer other = (ConsumerServer) obj;
			if (ip == null) {
				if (other.ip != null)
					return false;
			} else if (!ip.equals(other.ip))
				return false;
			if (port != other.port)
				return false;
			return true;
		}

		public boolean equalsIp(String ip) {
			if (!StringUtils.isBlank(this.ip)) {
				if (this.ip.equals(ip)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "ConsumerServer [ip=" + ip + ", hostName=" + hostName + ", port=" + port + "]";
		}

		public String getHostName() {
			return hostName;
		}

		public void setHostName(String hostName) {
			this.hostName = hostName;
		}

	}

	public static class ProducerServer {
		private String ip;

		private String hostName;

		public ProducerServer() {

		}

		public ProducerServer(String ip, String hostName) {
			this.ip = ip;
			this.hostName = hostName;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getHostName() {
			return hostName;
		}

		public void setHostName(String hostName) {
			this.hostName = hostName;
		}

	}

}

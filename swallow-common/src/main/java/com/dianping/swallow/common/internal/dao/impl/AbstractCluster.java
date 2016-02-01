package com.dianping.swallow.common.internal.dao.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.config.SwallowServerConfig;
import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:23:09
 */
public abstract class AbstractCluster extends AbstractLifecycle implements Cluster{

	protected final Logger logger = LogManager.getLogger(getClass());

	private List<InetSocketAddress>  seeds = new LinkedList<InetSocketAddress>();

	private String address;

	protected SwallowServerConfig swallowServerConfig;
	
	public AbstractCluster(String address) {
		
		this.address = address;
		this.seeds = build(address);
	}
	
	@Override
	public List<InetSocketAddress> getSeeds() {

		return seeds;
	}
	
	@Override
	public String getAddress() {
		return address;
	}

	/**
	 * 分割时考虑空白字符
	 * @param split
	 * @return
	 */
	protected String splitSpaces(String split) {
		return "\\s*"+split+"\\s*";
	}

	
	@Override
	public String toString() {
		return address;
	}

	
	@Override
	public void setSwallowServerConfig(SwallowServerConfig swallowServerConfig){
		this.swallowServerConfig = swallowServerConfig;
	}

	
	@Override
	public boolean sameCluster(Cluster other) {
		return sameCluster(this, other);
	}
	
	public static boolean sameCluster(Cluster cluster1, Cluster cluster2){

		if(cluster1 == cluster2){
			return true;
		}
		
		if(cluster1 != null && cluster2 != null){

			if(!cluster1.getClass().equals(cluster2.getClass())){
				return false;
			}
			
			List<InetSocketAddress> thisServers = cluster1.allServers();
			List<InetSocketAddress> otherServers = cluster2.allServers();

			for(InetSocketAddress address : thisServers){
				if(otherServers.contains(address)){
					return true;
				}
			}
		}
			
		return false;
	}

	protected List<InetSocketAddress> build(final String address) {
		
		String url = address.trim();
		String schema = getSchema();
		
		if(url.startsWith(schema)){
			url = url.substring(schema.length());
		}
		
		return buildAddress(url);
	}

	
	protected abstract String getSchema();

	protected List<InetSocketAddress> buildAddress(String address) {
		
		List<InetSocketAddress> result = new ArrayList<InetSocketAddress>();
		String[] hostPortArr = address.split(splitSpaces(","));
		
		for (int i = 0; i < hostPortArr.length; i++) {
			
			String[] pair = hostPortArr[i].split(splitSpaces(":"));
			if(pair.length != 2){
				throw new IllegalArgumentException("bad address:" + address);
			}
			try {
				result.add(new InetSocketAddress(pair[0].trim(), Integer.parseInt(pair[1].trim())));
			} catch (Exception e) {
				throw new IllegalArgumentException(
						e.getMessage()
								+ ". Bad format of store address："
								+ address
								+ ". The correct format is like " + addressExample(),
						e);
			}
		}
		
		return result;
	}

	protected String addressExample() {
		return getSchema() + "<host>:<port>,<host>:<port>";
	}

	
	/**
	 * ip:port,ip:port
	 * @param seeds
	 * @return
	 */
	protected String getAddressString(List<InetSocketAddress> seeds) {

		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i < seeds.size() ;i++){
			
			if(i > 0){
				sb.append(",");
			}
			InetSocketAddress address = seeds.get(i);
			sb.append(address.getHostName() + ":" + address.getPort());
		}
		
		return sb.toString();
	}


}

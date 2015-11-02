package com.dianping.swallow.common.internal.dao.impl;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:23:09
 */
public abstract class AbstractCluster extends AbstractLifecycle implements Cluster{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private List<InetSocketAddress>  seeds = new LinkedList<InetSocketAddress>();

	private String address;

	public AbstractCluster(String address) {
		
		this.address = address;
		this.seeds = build(address);
	}
	
	@Override
	public List<InetSocketAddress> getSeeds() {

		return seeds;
	}

	protected abstract List<InetSocketAddress> build(String url);

	
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

}

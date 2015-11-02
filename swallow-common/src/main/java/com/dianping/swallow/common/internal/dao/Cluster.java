package com.dianping.swallow.common.internal.dao;

import java.net.InetSocketAddress;
import java.util.List;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:22:23
 */
public interface Cluster extends Lifecycle{
	
	List<InetSocketAddress> getSeeds();

}

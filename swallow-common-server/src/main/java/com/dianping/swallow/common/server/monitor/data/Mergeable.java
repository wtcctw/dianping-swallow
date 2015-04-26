package com.dianping.swallow.common.server.monitor.data;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午7:14:08
 */
public interface Mergeable {

	void merge(Mergeable merge);
}

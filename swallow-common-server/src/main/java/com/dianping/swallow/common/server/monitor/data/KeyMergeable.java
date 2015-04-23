package com.dianping.swallow.common.server.monitor.data;

/**
 * 支持只merge某个key
 * @author mengwenchao
 *
 * 2015年4月21日 下午7:14:08
 */
public interface KeyMergeable extends Mergeable{

	void merge(String key, KeyMergeable merge);
}

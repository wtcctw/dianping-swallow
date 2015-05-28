package com.dianping.swallow.web.monitor;

import java.util.Set;

/**
 * @author mengwenchao
 *
 * 2015年5月28日 下午2:59:59
 */
public interface Retriever {

	int getKeepInMemoryHour();

	Set<String>  getTopics();

	Set<String>  getTopics(long start, long end);

}

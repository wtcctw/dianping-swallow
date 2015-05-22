package com.dianping.swallow.common.server.monitor.data.statis;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;

import com.dianping.swallow.common.server.monitor.data.Statisable;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午2:09:12
 */
public abstract class AbstractStatisable<V> implements Statisable<V>{

	@Transient
	protected transient final Logger logger = LoggerFactory.getLogger(getClass());

	
	protected void checkTypeMatch(Object merge) {
		
		if(merge == null || !(getClass().isAssignableFrom(merge.getClass()))){
			throw new IllegalArgumentException("wrong type " + merge);
		}
		
	}


	protected abstract Statisable<?> getValue(Object key);;

}

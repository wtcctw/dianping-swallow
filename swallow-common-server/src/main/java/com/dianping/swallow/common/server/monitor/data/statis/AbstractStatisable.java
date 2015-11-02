package com.dianping.swallow.common.server.monitor.data.statis;


import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;

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

	@Override
	public void removeBefore(Long time) {
		doRemoveBefore(time);
		cleanEmpty();
	}

	protected abstract void doRemoveBefore(Long time);

	protected abstract Statisable<?> getValue(Object key);

	public abstract void cleanEmpty();

	@Override
	public String toString() {
		return JsonBinder.getNonEmptyBinder().toPrettyJson(this);
	}
	
	public String toString(String key){
		
		return toString();
	}

	protected boolean isTotalKey(String key) {
		return key.equals(MonitorData.TOTAL_KEY);
	}

	@Override
	public void merge(String key, KeyMergeable merge) {
	}

	@Override
	public void merge(Mergeable merge) {
	}

	@Override
	public Object clone() throws CloneNotSupportedException{
		AbstractStatisable clone = (AbstractStatisable) super.clone();
		return clone;
	}

}

package com.dianping.swallow.common.server.monitor.data.structure;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.server.monitor.data.KeyMergeable;
import com.dianping.swallow.common.server.monitor.data.Mergeable;
import com.dianping.swallow.common.server.monitor.data.MonitorData;



/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午4:08:04
 */
public abstract class TotalMap<V extends Mergeable> extends ConcurrentHashMap<String, V> implements KeyMergeable{

	private static final long serialVersionUID = 1L;
	
    private final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected V total;
	
	public TotalMap(){
		super();
		
		total = createValue();
		put(MonitorData.TOTAL_KEY, total);
	}

	protected abstract V createValue();

	public V getTotal(){
		
		return total;
	}

	public void merge(Mergeable merge){
		
		checkType(merge);
		
		@SuppressWarnings("unchecked")
		TotalMap<Mergeable> toMerge = (TotalMap<Mergeable>) merge;
		
		for(java.util.Map.Entry<String, Mergeable> entry : toMerge.entrySet()){

			String key = entry.getKey();
			Mergeable value = entry.getValue();
		
			V myValue = get(key);
			if(myValue == null){
				myValue = createValue();
				put(key, myValue);
			}
			myValue.merge(value);
		}
	}

	public void merge(String key, KeyMergeable merge){
		checkType(merge);
		
		@SuppressWarnings("unchecked")
		TotalMap<Mergeable> toMerge = (TotalMap<Mergeable>) merge;
		Mergeable value = toMerge.get(key); 
		if( value == null){
			logger.warn("[merge][value null]" + key + "," + merge );
			return;
		}
		
		V myValue = get(key);
		if(myValue instanceof KeyMergeable && value instanceof KeyMergeable){
			
			((KeyMergeable)myValue).merge(key, (KeyMergeable)value);
		}else{
			
			myValue.merge(value);
		}
	}
	
	private void checkType(Mergeable merge) {
		
		if(!(merge instanceof TotalMap)){
			throw new IllegalArgumentException("wrong type : " + merge.getClass());
		}
	}

	@Override
	public String toString() {
		return JsonBinder.getNonEmptyBinder().toJson(this);
	}
}

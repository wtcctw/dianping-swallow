package com.dianping.swallow.common.server.monitor.data.structure;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.TotalBuilder;
import com.dianping.swallow.common.server.monitor.data.Totalable;
import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午4:08:04
 */
public abstract class TotalMap<V extends Mergeable> extends ConcurrentHashMap<String, V> implements KeyMergeable, Totalable, TotalBuilder, Cloneable{

	private static final long serialVersionUID = 1L;
	
    protected final Logger logger = LogManager.getLogger(getClass());
		
	public TotalMap(){
		super();
		createTotal();
	}

	protected abstract V createValue();

	@Override
	public V getTotal(){
		
		return get(MonitorData.TOTAL_KEY);
	}

	@Override
	public void buildTotal(){
		
		//每次重建
		V total = createTotal();
		
		for(Entry<String, V> entry : entrySet()){
			
			V value = entry.getValue();
			
			if((value instanceof TotalBuilder) && value != total){
				((TotalBuilder)value).buildTotal();
			}

			if(total != value){
				total.merge(value);
			}
		}
	}
	
	private V createTotal() {
		
		V total = createValue();
		if(total instanceof Totalable){
			((Totalable) total).setTotal();
		}
		put(MonitorData.TOTAL_KEY, total);
		return total;
	}

	public void merge(Mergeable merge){
		
		checkType(merge);
		
		@SuppressWarnings("unchecked")
		TotalMap<Mergeable> toMerge = (TotalMap<Mergeable>) merge;
		
		if(isTotal() && toMerge instanceof TotalBuilder){
			
			getTotal().merge((Mergeable)(((TotalBuilder)toMerge).getTotal()));
			return;
		}
		
		for(java.util.Map.Entry<String, Mergeable> entry : toMerge.entrySet()){

			String key = entry.getKey();
			Mergeable value = entry.getValue();
		
			if(isTotal()){
				getTotal().merge(value);
				continue;
			}
			V myValue = getOrCreate(key);
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
		
		V myValue = getOrCreate(key);
		if(myValue instanceof KeyMergeable && value instanceof KeyMergeable){
			
			((KeyMergeable)myValue).merge(key, (KeyMergeable)value);
		}else{
			
			myValue.merge(value);
		}
	}
	
	private V getOrCreate(String key) {
		V v = get(key);
		if(v == null){
			v= createValue();
			put(key, v);
		}
		return v;
	}

	private void checkType(Object merge) {
		
		if(!(merge instanceof TotalMap)){
			throw new IllegalArgumentException("wrong type : " + merge.getClass());
		}
	}

	@Override
	public String toString() {
		return JsonBinder.getNonEmptyBinder().toJson(this);
	}


	@JsonIgnore
	private boolean isTotal = false;
	
	@Override
	public void setTotal(){
		isTotal = true;
	}

	public boolean isTotal(){
		return isTotal;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException {
		
		TotalMap<Mergeable> map = (TotalMap<Mergeable>) super.clone();
		
		for(java.util.Map.Entry<String, ? extends Mergeable>  entry : map.entrySet()){
			
			String key = entry.getKey(); 
			Mergeable value = entry.getValue();
			map.put(key, (Mergeable) value.clone());
		}
		
		return map;
	}

}

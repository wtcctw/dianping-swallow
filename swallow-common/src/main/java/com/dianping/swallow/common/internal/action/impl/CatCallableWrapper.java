package com.dianping.swallow.common.internal.action.impl;

import java.util.concurrent.Callable;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.SwallowCallableWrapper;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午5:07:09
 */
public class CatCallableWrapper<V> implements SwallowCallableWrapper<V>{
	
	private String type;
	
	private String name;

	public CatCallableWrapper(String type, String name){
		this.type = type;
		this.name = name;
	}
	
	@Override
	public V doCallable(Callable<V> callable) throws Exception {
		
		Transaction t = Cat.newTransaction(type, name);
		
		try{
			V result = callable.call();
			t.setStatus(Transaction.SUCCESS);
			return result;
		}catch(Exception e){
			Cat.logError(e);
			t.setStatus(e);
			throw e;
		}finally{
			t.complete();
		}
	}
	
	

}

package com.dianping.swallow.common.internal.action.impl;

import java.util.concurrent.Callable;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.AbstractSwallowCallableWrapper;

/**
 * @author mengwenchao
 *
 * 2015年5月14日 下午7:41:31
 */
public class CatCallableWrapper<V> extends AbstractSwallowCallableWrapper<V>{
	
	private String type;
	
	private String name;

	public CatCallableWrapper(String type, String name){
		
		this.type = type;
		this.name = name;
	}


	@Override
	public V doCallable(Callable<V> callable) {
		
		Transaction t = Cat.newTransaction(type, name);
		
		V result = null;
		try{
			
			result = callable.call();
			t.setStatus(Transaction.SUCCESS);
		}catch(Throwable th){
			
			Cat.logError(th);
			t.setStatus(th);
			logger.error("[doAction]", th);
		}finally{
			t.complete();
		}
		
		return result;
	}

}

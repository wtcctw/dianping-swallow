package com.dianping.swallow.common.internal.action.impl;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.AbstractSwallowActionWrapper;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;

/**
 * @author mengwenchao
 *
 * 2015年5月14日 下午7:41:31
 */
public class CatActionWrapper extends AbstractSwallowActionWrapper implements SwallowActionWrapper{
	
	private String type;
	
	private String name;

	public CatActionWrapper(String type, String name){
		
		this.type = type;
		this.name = name;
	}

	@Override
	public void doAction(SwallowAction action) {
		
		Transaction t = Cat.newTransaction(type, name);
		
		try{
			
			action.doAction();
			t.setStatus(Transaction.SUCCESS);
		}catch(Throwable th){
			
			Cat.logError(th);
			t.setStatus(th);
			logger.error("[doAction]", th);
		}finally{
			t.complete();
		}
	}

}

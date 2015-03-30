package com.dianping.swallow.common.internal.action;

import com.dianping.cat.message.Transaction;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午4:25:14
 */
public interface SwallowCatActionWrapper {

	void doAction(Transaction transaction, SwallowAction action);
	
}

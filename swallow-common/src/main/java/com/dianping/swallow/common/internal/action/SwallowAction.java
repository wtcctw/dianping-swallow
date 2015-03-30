package com.dianping.swallow.common.internal.action;

import com.dianping.swallow.common.internal.exception.SwallowException;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午4:21:29
 */
public interface SwallowAction {
	
	void doAction() throws SwallowException;

}

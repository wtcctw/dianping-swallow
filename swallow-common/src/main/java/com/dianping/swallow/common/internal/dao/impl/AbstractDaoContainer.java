package com.dianping.swallow.common.internal.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.DAO;
import com.dianping.swallow.common.internal.dao.DAOContainer;

/**
 * @author mengwenchao
 *
 * 2015年11月3日 下午3:26:14
 */
public abstract class AbstractDaoContainer<T extends DAO<?>> implements DAOContainer<T>{
	
	private static final long serialVersionUID = 1L;
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public T getDao() {
		return getDao(false);
	}

	@Override
	public String toString() {
		
		return getDao().toString();
	}
	
}

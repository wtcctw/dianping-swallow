package com.dianping.swallow.common.internal.dao.impl;

import com.dianping.swallow.common.internal.dao.DAO;

/**
 * @author mengwenchao
 *
 * 2015年11月3日 下午3:27:41
 */
public class SingleDaoContainer<T extends DAO<?>> extends AbstractDaoContainer<T>{
	
	private static final long serialVersionUID = 1L;

	private final T  dao; 
	
	public SingleDaoContainer(T dao){
		this.dao = dao;
	}
	
	@Override
	public T getDao(boolean isRead) {
		return dao;
	}

}

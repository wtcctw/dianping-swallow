package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.SearchString;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:04:30
 */
public interface SearchPropDAO  {

	public void create(SearchString p);

    public SearchString readByDept(String dept) ;

	public void dropCol();
	
	public List<SearchString> findAll();
	
 }

package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.Administrator;

/**
 * @author mingdongli
 *
 * 2015年5月11日 上午11:05:13
 */
public interface VisitDao extends Dao{
	
	void saveVisit(Administrator p);
	
	Administrator readByVisitEmail(String email);
	
	List<Administrator> findAllVisit();
	
}

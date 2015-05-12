package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.Administrator;

public interface VisitDao extends Dao{
	
	void saveVisit(Administrator p);
	
	Administrator readByVisitEmail(String email);
	
	List<Administrator> findAllVisit();
	
}

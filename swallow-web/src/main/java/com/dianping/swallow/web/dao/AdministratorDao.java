package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.Administrator;


/**
 * @author mingdongli
 *
 * 2015年5月11日 上午11:14:30
 */
public interface AdministratorDao  extends VisitDao{

    Administrator readByEmail(String email);
    
    //use insert to create
    void createAdministrator(Administrator p);

    //use save to implement insert and update
	void saveAdministrator(Administrator p);
	
	int deleteByEmail(String email);  //txz is uniq
	
	void dropCol();
	
	List<Administrator> findAll();
	
	long countAdministrator();
	
	List<Administrator> findFixedAdministrator(int offset, int limit);
	
 }

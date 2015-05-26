package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.Administrator;


/**
 * @author mingdongli
 *
 * 2015年5月11日 上午11:14:30
 */
public interface AdministratorDao{

	/**
	 *  根据通行证查询Administrator列表
	 * @param name	通行证
	 */
    Administrator readByName(String name);
    
    /**
     * 新建Administrator纪录
     * @param admin  Administrator纪录
     */
    boolean createAdministrator(Administrator admin);

    /**
     * 保存Administrator纪录
     * @param admin  Administrator纪录
     */
	boolean saveAdministrator(Administrator admin);
	
	/**
	 * 根据通行证删除纪录
	 * @param name   通行证
	 */
	int deleteByName(String name);
	
	/**
	 * 删除集合
	 */
	void dropCol();
	
	/**
	 * 查询所有Administrator纪录
	 */
	List<Administrator> findAll();
	
	/**
	 * 查询Administrator列表中纪录条数
	 */
	long countAdministrator();
	
	/**
	 * 查询指定数目的Administrator纪录
	 * @param offset	起始位置
	 * @param limit		偏移量
	 */
	List<Administrator> findFixedAdministrator(int offset, int limit);
	
 }

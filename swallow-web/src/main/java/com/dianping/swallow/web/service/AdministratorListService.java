package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.Administrator;


/**
 * @author mingdongli
 *
 * 2015年5月21日下午8:30:48
 */
public interface AdministratorListService {

	/**
	 * 更新Administrator列表
	 * @param admin  管理员纪录
	 * @param name   通行证
	 * @param auth	 角色
	 */
	void updateAdmin(Administrator admin, String name, int auth);
	
	/**
	 * 新建Administrator列表
	 * @param name   通行证
	 * @param auth	 角色
	 */
	void doneCreateAdmin(String name, int auth);
}

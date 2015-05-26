package com.dianping.swallow.web.service;


/**
 * @author mingdongli
 *
 * 2015年5月21日下午8:30:48
 */
public interface AdministratorListService extends SwallowService{

	/**
	 * 更新Administrator列表
	 * @param admin  管理员纪录
	 * @param name   通行证
	 * @param auth	 角色
	 */
	boolean updateAdmin(String name, int auth);
}

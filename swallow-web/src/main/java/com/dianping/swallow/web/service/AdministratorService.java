package com.dianping.swallow.web.service;

import java.util.Map;

/**
 * @author mingdongli
 *
 * 2015年5月20日下午2:05:39
 */
public interface AdministratorService extends SwallowService{
	
	/**
	 * 查询指定数量的Administrator信息
	 * @param offset  起始位置
	 * @param limit	  偏移量
	 */
	Map<String, Object> queryAllRecordFromAdminList(int offset, int limit);

	/**
	 * 返回用户是否为管理员
	 * @param request
	 */
	Object queryIfAdmin(String userName);
	
	/**
	 * 创建用户
	 * @param name  用户通行证  
	 * @param auth  用户级别， 0 － 管理员，3 － 用户， 10 － 访问者
	 */
	boolean createInAdminList(String name, int auth);
	
	/**
	 * 根据通行证删除用户
	 * @param name 通行证
	 */
	boolean removeFromAdminList(String name);
	
	/**
	 * 查询所有访问者
	 */
	Object queryAllNameFromAdminList();
	
	
}

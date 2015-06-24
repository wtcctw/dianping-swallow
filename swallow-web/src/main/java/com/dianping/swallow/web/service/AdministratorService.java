package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.web.model.Administrator;

/**
 * @author mingdongli
 *
 * 2015年5月20日下午2:05:39
 */
public interface AdministratorService {
	
	/**
	 * 查询指定数量的Administrator信息
	 * @param offset  起始位置
	 * @param limit	  偏移量
	 */
	Map<String, Object> loadAdmin(int offset, int limit);
	
	List<Administrator> loadAllAdmin();

	
	/**
	 * 创建用户
	 * @param name  用户通行证  
	 * @param auth  用户级别， 0 － 管理员，3 － 用户， 10 － 访问者
	 */
	boolean createAdmin(String name, int auth);
	
	/**
	 * 根据通行证删除用户
	 * @param name 用户名
	 */
	boolean removeAdmin(String name);
	
	/**
	 * 查询所有访问者
	 */
	List<String> loadAllTypeName();
	
	Set<String> loadAdminSet();
	
	/**
	 * 更新Administrator列表
	 * @param admin  管理员纪录
	 * @param name   用户名
	 * @param auth	 角色
	 */
	boolean updateAdmin(String name, int auth);

	/**
	 * 
	 * @param username 用户名
	 */
	boolean recordVisitToAdmin(String username);
	
	String loadDefaultAdmin();
	
	
}

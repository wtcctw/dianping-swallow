package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.UserType;

/**
 * @author mingdongli
 *
 * 2015年5月20日下午2:05:39
 */
public interface UserService {
	
	/**
	 * 查询指定数量的Administrator信息
	 * @param offset  起始位置
	 * @param limit	  偏移量
	 */
	Pair<Long, List<Administrator>> loadUserPage(BaseDto baseDto);
	

	/**
	 * 
	 * @param username 用户名
	 */
	boolean createOrUpdateUser(String username);
	
	/**
	 * 创建用户
	 * @param username  用户通行证  
	 */
	boolean createUser(String username, UserType auth);
	
	/**
	 * 根据通行证删除用户
	 * @param name 用户名
	 */
	boolean removeUser(String name);
	
	/**
	 * 更新Administrator列表
	 * @param admin  管理员纪录
	 * @param name   用户名
	 * @param auth	 角色
	 */
	boolean updateUser(String name, UserType auth);

	List<Administrator> loadUsers();
	
	Set<String> loadCachedAdministratorSet();

}

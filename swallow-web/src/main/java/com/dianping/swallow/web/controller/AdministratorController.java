package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.service.AuthenticationService;
import com.dianping.swallow.web.service.UserService;

/**
 * @author mingdongli 2015年5月5日 下午2:42:57
 */
@Controller
public class AdministratorController extends AbstractMenuController {

	private static final String ADMINISTRATOR = "Administrator";
	private static final String USER = "User";

	@Resource(name = "userService")
	private UserService userService;

	@Autowired
	ExtractUsernameUtils extractUsernameUtils;

	@RequestMapping(value = "/console/administrator")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		
		return new ModelAndView("admin/index", createViewMap());
	}

	@RequestMapping(value = "/console/admin/auth/admindefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object adminDefault(int offset, int limit, String name, String role,
			HttpServletRequest request, HttpServletResponse response) {


		return userService.loadUserPage(offset, limit);
	}

	@RequestMapping(value = "/console/admin/auth/createadmin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void createAdmin(@RequestParam(value = "name") String name,
			@RequestParam(value = "role") String role,
			HttpServletRequest request, HttpServletResponse response) {

		int auth = convertRole(role);
		if(userService.createUser(name, auth)){
			logger.info(String.format("Create %s in administrator list successfully", name));
		}
		else{
			logger.info(String.format("Create %s in administrator list failed", name));
		}
	}

	@RequestMapping(value = "/console/admin/auth/removeadmin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void removeAdmin(@RequestParam(value = "name") String name,
			HttpServletRequest request, HttpServletResponse response) {

		if(userService.removeUser(name)){
			logger.info(String.format("Remove %s from administrator list successfully", name));
		}
		else{
			logger.info(String.format("Remove %s from administrator list failed", name));
		}
	}

	@RequestMapping(value = "/console/admin/queryvisits", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object queryAllVisits(HttpServletRequest request,
			HttpServletResponse response) {

		List<Administrator> adminList = userService.loadUsers();
		List<String> users = new ArrayList<String>();
		for(Administrator admin : adminList){
			String name = admin.getName();
			if(StringUtils.isNotBlank(name) && !users.contains(name)){
				users.add(name);
			}
		}
		return users;
	}

	private int convertRole(String role) {

		int auth = -1;
		if (!StringUtil.isEmpty(role)) {
			if (ADMINISTRATOR.equals(role.trim()))
				auth = AuthenticationService.ADMINI;
			else if (USER.equals(role.trim()))
				auth = AuthenticationService.USER;
			else
				auth = AuthenticationService.VISITOR;
		}
		return auth;
	}

	@Override
	protected String getMenu() {
		
		return "admin";
	}

}

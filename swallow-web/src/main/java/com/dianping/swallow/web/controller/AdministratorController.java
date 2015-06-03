package com.dianping.swallow.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.AccessControlServiceConstants;
import com.dianping.swallow.web.service.AdministratorService;

/**
 * @author mingdongli 2015年5月5日 下午2:42:57
 */
@Controller
public class AdministratorController extends AbstractMenuController {

	private static final String ADMINISTRATOR = "Administrator";
	private static final String USER = "User";

	@Resource(name = "administratorService")
	private AdministratorService administratorService;

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

		Map<String, Object> map = new HashMap<String, Object>();

		map = administratorService.queryAllRecordFromAdminList(offset, limit);
		return map;
	}

	@RequestMapping(value = "/console/admin/auth/createadmin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void createAdmin(@RequestParam(value = "name") String name,
			@RequestParam(value = "role") String role,
			HttpServletRequest request, HttpServletResponse response) {

		int auth = convertRole(role);
		if(administratorService.createInAdminList(name, auth)){
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

		if(administratorService.removeFromAdminList(name)){
			logger.info(String.format("Remove %s from administrator list successfully", name));
		}
		else{
			logger.info(String.format("Remove %s from administrator list failed", name));
		}
	}

	@RequestMapping(value = "/console/admin/queryadminandlogin", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object queryAdmin(HttpServletRequest request,
			HttpServletResponse response) {

		String username = extractUsernameUtils.getUsername(request);
		return administratorService.queryIfAdmin(username);
	}

	@RequestMapping(value = "/console/admin/queryvisits", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object queryAllVisits(HttpServletRequest request,
			HttpServletResponse response) {

		return administratorService.queryAllNameFromAdminList();
	}

	private int convertRole(String role) {

		int auth = -1;
		if (!StringUtil.isEmpty(role)) {
			if (ADMINISTRATOR.equals(role.trim()))
				auth = AccessControlServiceConstants.ADMINI;
			else if (USER.equals(role.trim()))
				auth = AccessControlServiceConstants.USER;
			else
				auth = AccessControlServiceConstants.VISITOR;
		}
		return auth;
	}

	@Override
	protected String getMenu() {
		
		return "admin";
	}

}

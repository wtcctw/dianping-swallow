package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.service.AccessControlServiceConstants;
import com.dianping.swallow.web.service.AdministratorService;
import com.dianping.swallow.web.service.impl.AccessControlServiceImpl;


/**
 * @author mingdongli
 *		2015年5月5日 下午2:42:57
 */
@Controller
public class AdministratorController extends AbstractController{
	
    private static final String 					ADMIN           			= "admin";
	private static final String             		SIZE                        = "size";
	private static final String 					ADMINISTRATOR 				= "Administrator";
	private static final String 					USER 						= "User";
	
	@Resource(name = "accessControlService")
	private AccessControlServiceImpl 				accessControlService;
	
    @Resource(name = "administratorService")
    private AdministratorService 					administratorService;
	
	@RequestMapping(value = "/console/administrator")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("admin/index", map);
	}
	
	@RequestMapping(value = "/console/admin/admindefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object adminDefault(String offset, String limit, String name, String role, 
			HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(accessControlService.checkVisitIsValid(request, null)){  //be able to access
			map = administratorService.queryAllFromAdminList(offset, limit);
			return map;
		}
		map = getZeroResult();
		return map;
	}
	
	
	@RequestMapping(value = "/console/admin/createadmin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void createAdmin(@RequestParam(value = "name") String name, @RequestParam(value = "role") String role,
			HttpServletRequest request, HttpServletResponse response) {
		
		if(accessControlService.checkVisitIsValid(request, null)){
			int auth = convertRole(role);
			administratorService.createInAdminList(name, auth);
		}
		return;
	}
	
	@RequestMapping(value = "/console/admin/removeadmin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void removeAdmin(@RequestParam(value = "name") String name,
					HttpServletRequest request, HttpServletResponse response) {

		if(accessControlService.checkVisitIsValid(request, null)){
			administratorService.removeFromAdminList(name);
		}
		
		return;
	}
	
	@RequestMapping(value = "/console/admin/queryadminandlogin", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object queryAdmin(HttpServletRequest request, HttpServletResponse response) {
		
		return administratorService.queryIfAdmin(request); 
	}
	
	@RequestMapping(value = "/console/admin/queryvisits", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object queryAllVisits(HttpServletRequest request, HttpServletResponse response) {
		
		return administratorService.queryAllNameFromAdminList();
	}
	
	private Map<String, Object> getZeroResult(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SIZE, 0);
		map.put(ADMIN, new ArrayList<Administrator>());
		return map;
	}
	
	private int convertRole(String role){ 
	
		int auth = -1;
		if(!StringUtil.isEmpty(role)){
			if(ADMINISTRATOR.equals(role.trim()))
				auth = AccessControlServiceConstants.ADMINI;
			else if(USER.equals(role.trim()))
				auth = AccessControlServiceConstants.USER;
			else
				auth = AccessControlServiceConstants.VISITOR;
		}
		return auth;
	}
	
}

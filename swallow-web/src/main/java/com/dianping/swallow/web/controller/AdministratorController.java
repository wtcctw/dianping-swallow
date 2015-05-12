package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.service.AccessControlService;


/**
 * @author mingdongli
 *		2015年5月5日 下午2:42:57
 */
@Controller
public class AdministratorController extends AbstractWriteDao {
	
    private static final String 			ADMIN           			= "admin";
	private static final String             SIZE                        = "size";
    private static final String 			LOGINNAME           		= "loginname";
	private static final String 			POST_EMAIL 					= "@dianping.com";
	private static final String 			TIMEFORMAT 					= "yyyy-MM-dd HH:mm";
	private static final String 			ADMINISTRATOR 				= "Administrator";
	private static final String 			LOGINDELIMITOR				= "\\|";
	
	private long 							searchSize 					= 0;
	private long 							totalNumOfTopic 			= 0;
	
	@Autowired
	private AdministratorDao 				admind;
	@Resource(name = "accessControlService")
	private AccessControlService 			accessControlService;
	
	private static final Logger logger = LoggerFactory
			.getLogger(AdministratorController.class);
	
	@RequestMapping(value = "/console/administrator")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("admin/index", map);
	}
	
	@RequestMapping(value = "/console/admin/admindefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object adminDefault(String offset, String limit, String name,
			String email, String role, HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Administrator> administratorList = new ArrayList<Administrator>();
		
		if(accessControlService.checkVisitIsValid(request, null)){
			int start = Integer.parseInt(offset);
			int span = Integer.parseInt(limit); // get span+1 admins
			boolean findAll = (name + email + role).isEmpty();
			if (findAll) {
				administratorList = getAllAdministratorFromExisting(start, span);
				searchSize = totalNumOfTopic;
			}
		}
		
		map.put(SIZE, searchSize);
		map.put(ADMIN, administratorList);
		return map;
	}
	
	public List<Administrator> getAllAdministratorFromExisting(int start, int span)
			throws UnknownHostException {
		totalNumOfTopic = admind.countAdministrator();
		List<Administrator> administratorList = admind.findAll();
		return administratorList;
	}
	
	@RequestMapping(value = "/console/admin/createadmin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void createAdmin(@RequestParam(value = "name") String name, @RequestParam(value = "email") String email,
			@RequestParam(value = "role") String role, HttpServletRequest request, HttpServletResponse response) {
		
		if(accessControlService.checkVisitIsValid(request, null)){
			int auth;
			if(role != null && ADMINISTRATOR.equals(role.trim()))
				auth = 0;
			else
				auth = 3;
			doCreateAdmin(name.trim(), email.trim(), auth);
		}
		return;
	}
	
	@RequestMapping(value = "/console/admin/removeadmin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void removeAdmin(@RequestParam(value = "name") String name, @RequestParam(value = "email") String email,
					HttpServletRequest request, HttpServletResponse response) {
		if(accessControlService.checkVisitIsValid(request, null)){
			int n = admind.deleteByEmail(email);
			if(n != 1){
				if(logger.isInfoEnabled()){
					logger.info("deleteByEmail is wrong with email: " + email);
				}
			}
			else{
				if(logger.isInfoEnabled()){
					logger.info("delete administrator with name [" + name + "] and email [" + email + "].");
				}
			}
		}
		return;
	}
	
	@RequestMapping(value = "/console/admin/queryadminandlogin", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object queryAdmin(HttpServletRequest request, HttpServletResponse response) {
		
		StringBuffer username = new StringBuffer();
		StringBuffer txz = new StringBuffer();
		
		setVisitInfo(request, username, txz);
		
		return setResult(txz.toString(), username.toString());
	}
	
	@RequestMapping(value = "/console/admin/queryvisits", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object queryAllAdmins(HttpServletRequest request, HttpServletResponse response) {
		
		List<String> adminLists = new ArrayList<String>();
		List<Administrator> admins = admind.findAllVisit(); //extract info from swallowwebvisitc
		for(int i = 0; i < admins.size(); ++i){
			adminLists.add(admins.get(i).getName());
		}
		return adminLists;
	}
	
	private boolean getEnv(){
		String env = EnvZooKeeperConfig.getEnv();
		boolean notproduct = false;
		if(!"product".equals(env))  //not product
			notproduct = true;
		if(logger.isInfoEnabled()){
			logger.info("Lion env is " + env);
		}
		accessControlService.setShowContentToAll(notproduct);
		return notproduct;
	}
	
	private Map<String, Object> setResult(String txz, String username){
		boolean notproduct = getEnv();
		Map<String, Object> map = new HashMap<String,Object>();
		Administrator a = admind.readByEmail(txz + POST_EMAIL); //read from swallowwebadminc
		if(a != null && a.getRole() == 0){ //record exist and role is 0
			map.put(ADMIN, true);
			saveNewVisit(username, txz + POST_EMAIL, 0); //also save info in swallowwebvisitc
		}
		else{
			map.put(ADMIN, false | notproduct);  //show all content if not product
			if(a == null){  //have no record in swallowwebadminc, query from swallowwebvisitc
				saveNewVisit(username, txz + POST_EMAIL, 3); //first access, set role 3, or do nothing
			}
		}
			
		map.put(LOGINNAME, username);
		return map;
	}
	
	private void saveNewVisit(String username, String email, int auth){
		Administrator visit = admind.readByVisitEmail(email);
		if(visit == null){
			visit = getAdministrator(username, email, auth);
			admind.saveVisit(visit);
			if(logger.isInfoEnabled()){
				logger.info("Save visit " + visit);
			}
		}
	}
	
	private void doCreateAdmin(String name, String email, int auth){
		Administrator a = admind.readByEmail(email);
		if(a == null){ //create new administrator
			a = getAdministrator(name, email, auth);
			admind.createAdministrator(a);
			if(logger.isInfoEnabled()){
				logger.info("create admin: " + a);
			}
		}
		else{ //update
			a.setName(name);
			a.setEmail(email);
			a.setRole(auth);
			a.setDate(new SimpleDateFormat(TIMEFORMAT).format(new Date()));
			admind.saveAdministrator(a);
			if(logger.isInfoEnabled()){
				logger.info("update admin to : " + a);
			}
		}
		return;
	}
	
	private Administrator getAdministrator(String name, String email, int role) {
		//Long id = System.currentTimeMillis();
		String date = new SimpleDateFormat(TIMEFORMAT).format(new Date());
		Administrator a = new Administrator(name, email, role, date);
		return a;
	}
	
	public void setVisitInfo(HttpServletRequest request, StringBuffer username, StringBuffer txz){
		String tmpusername = request.getRemoteUser();
		if (tmpusername == null){ 
		      username.append("");
		      txz.append("");
		}
	    else{
	    	String[] userinfo = tmpusername.split(LOGINDELIMITOR);
	    	username.append(userinfo[userinfo.length - 1]);
	    	txz.append(userinfo[0]);
	    }
	}
}

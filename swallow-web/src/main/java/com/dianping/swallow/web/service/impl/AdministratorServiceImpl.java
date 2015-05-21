package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.controller.utils.WebSwallowUtils;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.TopicDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.AccessControlServiceConstants;
import com.dianping.swallow.web.service.AdministratorListService;
import com.dianping.swallow.web.service.AdministratorService;


/**
 * @author mingdongli
 *
 * 2015年5月14日下午8:04:43
 */
@Service("administratorService")
public class AdministratorServiceImpl extends AbstractSwallowService implements AdministratorService {

    private static final String 					ADMIN           			= "admin";
	private static final String             		SIZE                        = "size";
    private static final String 					LOGINNAME           		= "loginname";
	@Autowired
	private TopicDao 								topicDao;
	@Autowired
	private AdministratorDao 						administratorDao;
	@Resource(name = "administratorListService")
	private AdministratorListService 				administratorListService;
	
	@Override
	public Map<String, Object> queryAllFromAdminList(String offset, String limit){
		
		
		int start = Integer.parseInt(offset);
		int span  = Integer.parseInt(limit);
			
		return getFixedAdministratorFromExisting(start, span);
	}
	
	
	private Map<String, Object> getFixedAdministratorFromExisting(int start, int span){

		Long totalNumOfTopic = administratorDao.countAdministrator();
		List<Administrator> administratorList = administratorDao.findFixedAdministrator(start, span);
		
		return getResponse(administratorList, totalNumOfTopic);
	}
	
	private Map<String, Object> getResponse(List<Administrator> administratorList, Long adminSize){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SIZE, adminSize);
		map.put(ADMIN, administratorList);
		return map;
	}
	
	@Override
	public void createInAdminList(String name, int auth) {

		doCreateAdmin(name.trim(), auth);
		return;
	}
	
	@Override
	public void removeFromAdminList(String name) {

		int n = administratorDao.deleteByName(name);
		if(n != 1){
			if(logger.isInfoEnabled()){
				logger.info("deleteByEmail is wrong with email: " + name);
			}
		}
		else{
			if(logger.isInfoEnabled()){
				logger.info("delete administrator with name [" + name + "]");
			}
		}
	}
	
	@Override
	public Object queryIfAdmin(HttpServletRequest request) {
		
		return setResult(WebSwallowUtils.getVisitInfo(request));
	}
	
	@Override
	public Object queryAllNameFromAdminList() {
		
		List<String> adminLists = new ArrayList<String>();
		List<Administrator> admins = administratorDao.findAll(); //extract info from swallowwebadminc
		for(int i = 0; i < admins.size(); ++i){
			adminLists.add(admins.get(i).getName());
		}
		return adminLists;
	}
	
	private Map<String, Object> setResult(String tongXingZheng){
		boolean notproduct = isShowContentToAll();
		if(logger.isInfoEnabled()){
			logger.info("notproduct is " + notproduct);
		}
		Map<String, Object> map = new HashMap<String,Object>();
		Administrator a = administratorDao.readByName(tongXingZheng); //read from swallowwebadminc
		if(a != null && a.getRole() == 0){ //record exist and role is 0
			map.put(ADMIN, true);
		}
		else{
			map.put(ADMIN, false | notproduct);  //show all content if not product
			if(a == null){  //have no record in swallowwebadminc
				if(tongXingZheng.equals(getDefaultAdmin()))
					saveNewVisit(tongXingZheng , AccessControlServiceConstants.ADMINI);
				else
					saveNewVisit(tongXingZheng , AccessControlServiceConstants.VISITOR); //first access, set role 10, or do nothing
			}
		}
			
		map.put(LOGINNAME, tongXingZheng);
		return map;
	}
	
	private void saveNewVisit(String name, int auth){
		
		createInAdminList(name, auth);	
	}
	
	private void doCreateAdmin(String name, int auth){
		Administrator a = administratorDao.readByName(name);
		if(a == null){
			administratorListService.doneCreateAdmin(name, auth);
		}
		else{ //update 
			administratorListService.updateAdmin(a, name, auth);
		}
		return;
	}
	
}

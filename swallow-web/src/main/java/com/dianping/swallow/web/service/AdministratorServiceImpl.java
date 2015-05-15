package com.dianping.swallow.web.service;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.controller.utils.WebSwallowUtils;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.model.Administrator;


/*-
 * @author mingdongli
 *
 * 2015年5月14日下午8:04:43
 */
@Service("administratorService")
public class AdministratorServiceImpl extends AbstractSwallowService implements AdministratorService {

    private static final String 					ADMIN           			= "admin";
	private static final String             		SIZE                        = "size";
    private static final String 					LOGINNAME           		= "loginname";
	private static final String 					TIMEFORMAT 					= "yyyy-MM-dd HH:mm";

	
	@Autowired
	private AdministratorDao 						admind;
	
	@Override
	public Map<String, Object> adminQuery(String offset, String limit, String name, String role) throws UnknownHostException {
		
		
		int start = Integer.parseInt(offset);
		int span = Integer.parseInt(limit);
			
		return getFixedAdministratorFromExisting(start, span);
	}
	
	
	private Map<String, Object> getFixedAdministratorFromExisting(int start, int span)
			throws UnknownHostException {
		
		Long totalNumOfTopic = admind.countAdministrator();
		List<Administrator> administratorList = admind.findFixedAdministrator(start, span);
		
		return getResponse(administratorList, totalNumOfTopic);
	}
	
	private Map<String, Object> getResponse(List<Administrator> administratorList, Long adminSize){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SIZE, adminSize);
		map.put(ADMIN, administratorList);
		return map;
	}
	
	@Override
	public void createAdmin(String name, int auth) {

		doCreateAdmin(name.trim(), auth);
		return;
	}
	
	@Override
	public void removeAdmin(String name) {

		int n = admind.deleteByName(name);
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
	public Object queryAdmin(HttpServletRequest request) {
		
		List<String> info = WebSwallowUtils.getVisitInfo(request);
		
		return setResult(info.get(1), info.get(0));
	}
	
	@Override
	public Object queryAllVisits() {
		
		List<String> adminLists = new ArrayList<String>();
		List<Administrator> admins = admind.findAll(); //extract info from swallowwebadminc
		for(int i = 0; i < admins.size(); ++i){
			adminLists.add(admins.get(i).getName());
		}
		return adminLists;
	}
	
	private Map<String, Object> setResult(String txz, String username){
		boolean notproduct = isShowContentToAll();  //getEnv();
		if(logger.isInfoEnabled()){
			logger.info("notproduct is " + notproduct);
		}
		Map<String, Object> map = new HashMap<String,Object>();
		Administrator a = admind.readByName(txz); //read from swallowwebadminc
		if(a != null && a.getRole() == 0){ //record exist and role is 0
			map.put(ADMIN, true);
		}
		else{
			map.put(ADMIN, false | notproduct);  //show all content if not product
			if(a == null){  //have no record in swallowwebadminc
				if(txz.equals(getDefaultAdmin()))
					saveNewVisit(txz , AccessControlServiceConstants.ADMINI);
				else
					saveNewVisit(txz , AccessControlServiceConstants.VISITOR); //first access, set role 10, or do nothing
			}
		}
			
		map.put(LOGINNAME, username);
		return map;
	}
	
	private void saveNewVisit(String name, int auth){
		
		createAdmin(name, auth);	
	}
	
	private void doCreateAdmin(String name, int auth){
		Administrator a = admind.readByName(name);
		if(a == null){ //create new administrator
			doneCreateAdmin(name, auth);
		}
		else{ //update 
				updateAdmin(a, name, auth);
		}
		return;
	}
	
	private void doneCreateAdmin(String name, int auth){
		Administrator admin = getAdministrator(name, auth);
		admind.createAdministrator(admin);
		if(logger.isInfoEnabled()){
			logger.info("create admin: " + admin);
		}
	}
	
	private void updateAdmin(Administrator admin, String name, int auth){
		admin.setName(name);
		admin.setRole(auth);
		admin.setDate(new SimpleDateFormat(TIMEFORMAT).format(new Date()));
		admind.saveAdministrator(admin);
		if(logger.isInfoEnabled()){
			logger.info("update admin to : " + admin);
		}
	}
	
	private Administrator getAdministrator(String name, int role) {
		String date = new SimpleDateFormat(TIMEFORMAT).format(new Date());
		Administrator a = new Administrator(name, role, date);
		return a;
	}


	@Override
	public void saveVisitAdmin(String name) {
		Administrator admin = admind.readByName(name);
		if(admin != null)
			updateAdmin(admin, name, admin.getRole()); //update time
		else
			doneCreateAdmin(name, AccessControlServiceConstants.VISITOR);
		
		return;
	}

}

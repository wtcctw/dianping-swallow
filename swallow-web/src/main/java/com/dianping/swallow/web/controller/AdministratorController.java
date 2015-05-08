package com.dianping.swallow.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.dao.MongoManager;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


/**
 * @author mingdongli
 *		2015年5月5日 下午2:42:57
 */
@Controller
public class AdministratorController extends AbstractController{
	
	private static final String 			TOPIC_DB_NAME 				= "swallowwebapplication";
    private static final String 			ADMIN_COLLECTION            = "swallowwebadminc";
    private static final String 			ADMINNAME           		= "adminname";
    private static final String 			LOGINNAME           		= "loginname";
	private static final String 			LOGINDELIMITOR				= "\\|";
	private MongoOperations 				writeMongoOps;
	private MongoClient 					writeMongo;
	
	private static final Logger logger = LoggerFactory
			.getLogger(TopicController.class);
	
	@RequestMapping(value = "/console/administrator")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("admin/index", map);
	}
	
	@RequestMapping(value = "/console/editadmin", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void saveAdmin(@RequestParam(value = "name") String name,
							HttpServletRequest request, HttpServletResponse response) {
		String namelist = name.trim().replace("\"", "");
		saveAdmin(namelist);
		return;
	}
	
	@RequestMapping(value = "/console/queryadminandlogin", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object queryAdmin(HttpServletRequest request, HttpServletResponse response) {
		String username = null;
		String tmpusername = request.getRemoteUser();
		if (tmpusername == null) 
		      username = "";
	    else{
	    	String[] userinfo = tmpusername.split(LOGINDELIMITOR);
	    	username = userinfo[userinfo.length - 1];
	    }
		String admin  = queryAdmin();
		Map<String, String> map = new HashMap<String,String>();
		map.put(LOGINNAME, username);
		map.put(ADMINNAME, admin);
		return map;
	}
	
	private void saveAdmin(String admin) {
		DBCollection collection = getDBCollection();
		BasicDBObject document = new BasicDBObject();
		document.put(ADMINNAME, admin.trim());
		collection.save(document);
		if(logger.isInfoEnabled()){
			logger.info("Update admin to " + admin.trim());
		}
	}
	
	private String queryAdmin() {
		DBCollection collection = getDBCollection();
		DBObject dbObject = collection.findOne();
		String namelist = "";
		if(dbObject != null)
			namelist = (String) dbObject.get(ADMINNAME);
		if(logger.isInfoEnabled()){
			logger.info("Query admin");
		}
		return namelist;
	}
	
	private DBCollection getDBCollection(){
		writeMongoOps = getWriteMongoOps();
		return writeMongoOps.getCollection(ADMIN_COLLECTION);
	}

	private MongoOperations getWriteMongoOps() {
		if (writeMongoOps == null) // in case it is empty
			if (writeMongo == null){ // in case it is empty
				writeMongo = MongoManager.getInstance().getWriteMongo();
			}
		writeMongoOps = new MongoTemplate(new SimMongoDbFactory(writeMongo,
				TOPIC_DB_NAME));
		return writeMongoOps;
	}
	
}

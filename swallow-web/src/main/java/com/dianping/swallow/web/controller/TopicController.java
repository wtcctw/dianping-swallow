package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.mongodb.MongoClient;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.TopicDao;
import com.dianping.swallow.web.dao.WebSwallowMessageDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.service.AccessControlService;

/**
 * @author mingdongli
 *
 *         2015年4月22日 下午1:50:20
 */
@Controller
public class TopicController extends AbstractWriteDao {

	private static final String             SIZE                        = "size";
	private static final String             TOPIC                       = "topic";
	public  static final String 			TIMEFORMAT 					= "yyyy-MM-dd HH:mm";

	private static final String 			PRE_MSG 					= "msg#";
	private static final String 			DELIMITOR					= ",";

	private List<MongoClient> 				allReadMongo 				= new ArrayList<MongoClient>();
	private long 							totalNumOfTopic 			= 0;
	private long 							searchSize 					= 0;
	
	@Autowired
	private WebSwallowMessageDao 			smdi;
	@Autowired
	private TopicDao 						tdi;
	@Autowired
	private AdministratorDao 				admind;
	@Resource(name="accessControlService")
	private AccessControlService accessControlService;

	
	private static final Logger logger = LoggerFactory
			.getLogger(TopicController.class);

	@RequestMapping(value = "/console/topic")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("topic/index", map);
	}
	
	@RequestMapping(value = "/console/topic/topicdefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object topicDefault(String offset, String limit, String name,
			String prop, String dept, HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {

		int start = Integer.parseInt(offset);
		int span = Integer.parseInt(limit); // get span+1 topics so that it can
		List<Topic> topicList = null;
		Map<String, Object> map = new HashMap<String, Object>();
		boolean findAll = (name + prop + dept).isEmpty();
		if (findAll) {
			topicList = getAllTopicFromExisting(start, span);
			searchSize = totalNumOfTopic;
		} else {
			topicList = getSpecificTopic(start, span, name, prop, dept); // name
			searchSize = topicList.size();
		}
		
		map.put(SIZE, searchSize);
		map.put(TOPIC, topicList);
		return map;
	}

	// read records from writeMongoOps dut to it alread exists
	public List<Topic> getAllTopicFromExisting(int start, int span)
			throws UnknownHostException {
		totalNumOfTopic = tdi.countTopic();
		List<Topic> topicList = tdi.findFixedTopic(start, span);
		return topicList;
	}

	// read from readMongoOps
	public Topic createOneTopic(String dbn) {
		String subStr = dbn.substring(PRE_MSG.length());
		long num = smdi.count(subStr); // 关联的message数目
		return getTopic(subStr, num);
	}

	public Topic getTopic(String subStr, long num) {
		Long id = System.currentTimeMillis();
		String date = new SimpleDateFormat(TIMEFORMAT).format(new Date());
		Topic p = new Topic(id.toString(), subStr, "", "", date, num);
		return p;
	}

	// just read, so use writeMongoOps
	public List<Topic> getSpecificTopic(int start, int span, String name,
			String prop, String dept) throws UnknownHostException {

		List<Topic> specificT = tdi.findSpecific(name, prop, dept);
		return specificT;
	}

	// read from readMongoOps
	@RequestMapping(value = "/console/topic/namelist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object topicName() throws UnknownHostException {
		List<String> tmpDBName = new ArrayList<String>();
		allReadMongo = smdi.getAllReadMongo();
		for (MongoClient mc : allReadMongo) {
			tmpDBName.addAll(mc.getDatabaseNames());
		}

		List<String> dbName = new ArrayList<String>();
		for (String dbn : tmpDBName) {
			if (dbn.startsWith(PRE_MSG)){
				String str = dbn.substring(PRE_MSG.length());
				if(!dbName.contains(str))
					dbName.add(str);
			}
		}
		return dbName;
	}

	// read from writeMongoOps, everytime read the the database to get the latest info
	@RequestMapping(value = "/console/topic/deptlist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object deptName() throws UnknownHostException {
		List<String> depts = new ArrayList<String>();
		List<Topic> topics = tdi.findAll();
		for(int i = 0; i < topics.size(); ++i){
			depts.add(topics.get(i).getDept());
		}
		return depts;
	}

	// read from writeMongoOps, everytime read the the database to get the latest info
	@RequestMapping(value = "/console/topic/proplist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object propName() throws UnknownHostException {
		List<String> props = new ArrayList<String>();
		List<Topic> topics = tdi.findAll();
		for(int i = 0; i < topics.size(); ++i){
			String[] tmpprops = topics.get(i).getProp().split(DELIMITOR);
			for(int j = 0; j < tmpprops.length; ++j){
				if(!props.contains(tmpprops[j]))
					props.add(tmpprops[j]);
			}
		}
		return props;
	}
	
	@RequestMapping(value = "/console/topic/edittopic", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void sendGroupMessages(@RequestParam(value = "name") String name, @RequestParam("prop") String prop, 
							@RequestParam("dept") String dept,@RequestParam("time") String time,
							HttpServletRequest request, HttpServletResponse response) {
		
		if(accessControlService.checkVisitIsValid(request)){
		accessControlService.getTopicToWhiteList().put(name, splitProps(prop.trim()));
			if(logger.isInfoEnabled()){
				logger.info("Update prop to " + splitProps(prop) + " of " + name);
			}
			tdi.updateTopic(name, prop, dept, time);
		}

		// return current refreshed page
		return;
	}
	
	private Set<String> splitProps(String props) {
		String[] prop = props.split(DELIMITOR);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));
		return lists;
	}

}
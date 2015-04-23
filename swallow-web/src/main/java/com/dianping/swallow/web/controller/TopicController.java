package com.dianping.swallow.web.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.dianping.swallow.web.dao.SearchPropDAOImpl;
import com.dianping.swallow.web.dao.WebSwallowMessageDAOImpl;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.dianping.swallow.web.dao.MongoManager;
import com.dianping.swallow.web.dao.TopicDAOImpl;
import com.dianping.swallow.web.model.SearchProp;
import com.dianping.swallow.web.model.Topic;

/**
 * @author mingdongli
 *
 *         2015年4月22日 下午1:50:20
 */
@Controller
@Component
public class TopicController extends AbstractController {

	private static final String 			TOPIC_DB_NAME 				= "swallowwebapplication";
	private static final String 			DEPT_COLLECTION 			= "cdept";
	private static final String 			DEPT_NAME 					= "dept";
	private static final String             DEFAULT                     = "default";
	private static final String             SIZE                        = "size";
	private static final String             TOPIC                       = "topic";

	private static final String 			PROP_COLLECTION 			= "cprop";
	private static final String 			PROP_NAME 					= "prop";
	public  static final String 			TIMEFORMAT 					= "yyyy-MM-dd";

	private static final String 			PRE_MSG 					= "msg#";

	private Map<String, MongoClient> 		topicNameToMongoMap 		= new HashMap<String, MongoClient>();
	private List<MongoClient> 				allReadMongo 				= new ArrayList<MongoClient>();
	private MongoClient 					writeMongo;
	private WebSwallowMessageDAOImpl 		smdi;
	private TopicDAOImpl 					tdi;
	private SearchPropDAOImpl 				ddi;
	private MongoOperations 				readMongoOps;
	private MongoOperations 				writeMongoOps;
	private volatile boolean 				isTopicDbexist 				= false;
	private long 							totalNumOfTopic 			= 0;
	private long 							searchSize 					= 0;

	private static final Logger logger = LoggerFactory
			.getLogger(TopicController.class);

	@RequestMapping(value = "/topic/topicdefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object topicDefault(String offset, String limit, String name,
			String prop, String dept) throws UnknownHostException {

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
		//writeMongo.dropDatabase(TOPIC_DB_NAME);
		map.put(SIZE, searchSize);
		map.put(TOPIC, topicList);
		Gson gson = new Gson(); // for last page to return
		return gson.toJson(map);
	}

	// read records from writeMongoOps dut to it alread exists
	public List<Topic> getAllTopicFromExisting(int start, int span)
			throws UnknownHostException {
		MongoOperations writeMongoOps = getWriteMongoOps();
		tdi = new TopicDAOImpl(writeMongoOps);
		totalNumOfTopic = tdi.countTopic();
		List<Topic> topicList = tdi.findFixedTopic(start, span);
		return topicList;
	}

	private MongoOperations getWriteMongoOps() {
		if (writeMongoOps == null) // in case it is empty
			if (writeMongo == null) // in case it is empty
				writeMongo = MongoManager.getInstance().getWriteMongo();
		writeMongoOps = new MongoTemplate(new SimMongoDbFactory(writeMongo,
				TOPIC_DB_NAME));
		return writeMongoOps;
	}


	private MongoClient getMongoFromMap(String dbn) {
		if (topicNameToMongoMap.get(dbn) == null)
			return topicNameToMongoMap.get(DEFAULT);
		return topicNameToMongoMap.get(DEFAULT);
	}

	// read from readMongoOps
	public Topic createOneTopic(String dbn) {
		String subStr = dbn.substring(PRE_MSG.length());
		readMongoOps = new MongoTemplate(new SimMongoDbFactory(
				getMongoFromMap(subStr), dbn)); // write in writeMongo
		smdi = new WebSwallowMessageDAOImpl(readMongoOps);
		long num = smdi.count(); // 关联的message数目
		return getTopic(subStr, num);
	}

	public static Topic getTopic(String subStr, long num) {
		Long id = System.currentTimeMillis();
		String date = new SimpleDateFormat(TIMEFORMAT).format(new Date());
		Topic p = new Topic(id.toString(), subStr, "", "", date, num);
		return p;
	}

	// just read, so use writeMongoOps
	public List<Topic> getSpecificTopic(int start, int span, String name,
			String prop, String dept) throws UnknownHostException {

		MongoOperations writeMongoOps = getWriteMongoOps();
		tdi = new TopicDAOImpl(writeMongoOps);
		List<Topic> specificT = tdi.findSpecific(name, prop, dept);
		return specificT;
	}

	// read from readMongoOps
	@RequestMapping(value = "/topic/namelist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object topicName() throws UnknownHostException {
		List<String> tmpDBName = new ArrayList<String>();
		allReadMongo = MongoManager.getInstance().getAllReadMongo();
		for (MongoClient mc : allReadMongo) {
			tmpDBName.addAll(mc.getDatabaseNames());
		}

		List<String> dbName = new ArrayList<String>();
		for (String dbn : tmpDBName) {
			if (dbn.startsWith(PRE_MSG))
				dbName.add(dbn.substring(PRE_MSG.length()));
		}
		Gson gson = new Gson();
		return gson.toJson(dbName);
	}

	// read from writeMongoOps, everytime read the the database to get the latest info
	@RequestMapping(value = "/topic/deptlist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object deptName() throws UnknownHostException {
		MongoOperations writeMongoOps = getWriteMongoOps();
		ddi = new SearchPropDAOImpl(writeMongoOps, DEPT_COLLECTION, DEPT_NAME);
		List<SearchProp> deptList = ddi.findAll();
		List<String> depts = new ArrayList<String>();
		for (SearchProp d : deptList)
			depts.add(d.getDept());

		Gson gson = new Gson();
		return gson.toJson(depts);
	}

	// read from writeMongoOps, everytime read the the database to get the
	// latest info
	@RequestMapping(value = "/topic/proplist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object propName() throws UnknownHostException {
		MongoOperations writeMongoOps = getWriteMongoOps();
		ddi = new SearchPropDAOImpl(writeMongoOps, PROP_COLLECTION, PROP_NAME);
		List<SearchProp> propList = ddi.findAll();
		List<String> props = new ArrayList<String>();
		for (SearchProp p : propList)
			props.add(p.getDept());

		Gson gson = new Gson();
		return gson.toJson(props);
	}

	// write use writeMongoOps
	private void insertDept(String dept) {
		if (!dept.isEmpty()) {
			MongoOperations writeMongoOps = getWriteMongoOps();
			ddi = new SearchPropDAOImpl(writeMongoOps, DEPT_COLLECTION,
					DEPT_NAME);
			if (ddi.readByDept(dept) == null) {
				SearchProp d = createSearchProp(dept);
				ddi.create(d);
			}
		}
	}

	// write use writeMongoOps
	private void insertProp(String prop) {
		if (!prop.isEmpty()) {
			MongoOperations writeMongoOps = getWriteMongoOps();
			ddi = new SearchPropDAOImpl(writeMongoOps, PROP_COLLECTION,
					PROP_NAME);
			if (ddi.readByDept(prop) == null) {
				SearchProp p = createSearchProp(prop);
				ddi.create(p);
			}
		}
	}

	private SearchProp createSearchProp(String search) {
		Long id = System.currentTimeMillis();
		SearchProp sp = new SearchProp(id.toString(), search);
		return sp;
	}

	// write use writeMongoOps
	@RequestMapping(value = "/edittopic")
	public RedirectView edit(String name, String prop, String dept,
			String time, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException,
			IOException {

		MongoOperations writeMongoOps = getWriteMongoOps();
		tdi = new TopicDAOImpl(writeMongoOps);
		tdi.updateTopic(name, prop, dept, time);
		// update DEPT_DB_NAME and PROP_DB_NAME
		insertDept(dept);
		insertProp(prop);
		// return current refreshed page
		return new RedirectView(request.getContextPath());
	}

	private boolean isTopicName(String str) {
		if (str != null && str.startsWith(PRE_MSG))
			return true;
		else
			return false;
	}

	@Scheduled(cron = "0/10 * * * * MON-FRI")
	public void scanTopicDatabase() {
		topicNameToMongoMap = MongoManager.getInstance()
				.getTopicNameToMongoMap(); // name starts without msg#
		allReadMongo = MongoManager.getInstance().getAllReadMongo();
		writeMongo = MongoManager.getInstance().getWriteMongo();
		List<String> writeDbNames = writeMongo.getDatabaseNames();
		if (writeDbNames.contains(TOPIC_DB_NAME)) {
			isTopicDbexist = true;
		}
		List<String> dbs = new ArrayList<String>();
		for (MongoClient mc : allReadMongo) {
			dbs.addAll(mc.getDatabaseNames());
		}
		Collections.sort(dbs);
		MongoOperations writeMongoOps = getWriteMongoOps();
		tdi = new TopicDAOImpl(writeMongoOps);
		if (isTopicDbexist) { // update
			for (String str : dbs) {
				String subStr = str.substring(PRE_MSG.length());
				if (isTopicName(str) && tdi.readByName(subStr) == null) {
					tdi.create(TopicController.getTopic(subStr, 0L));
					logger.info("create topic : "
							+ TopicController.getTopic(subStr, 0L));
				}

			}
//			writeMongo.dropDatabase(TOPIC_DB_NAME);
		} else { // create
			for (String dbn : dbs) {
				if (isTopicName(dbn)) {
					String subStr = dbn.substring(PRE_MSG.length());
					Topic t = TopicController.getTopic(subStr, 0L);
					tdi.create(t);
				}
			}
		}
	}

}
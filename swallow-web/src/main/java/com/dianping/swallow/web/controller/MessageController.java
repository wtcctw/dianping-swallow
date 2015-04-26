package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.dianping.swallow.web.dao.MongoManager;
import com.dianping.swallow.web.dao.SimMongoDbFactory;
import com.dianping.swallow.web.dao.impl.DefaultWebSwallowMessageDAO;
import com.dianping.swallow.web.model.WebSwallowMessage;


/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:04:03
 */
@Controller
public class MessageController extends AbstractController {
	
	private static final String 			PRE_MSG						= "msg#";
	private static final String             DEFAULT                     = "default";
	private static final String             SIZE                        = "size";
	private static final String             MESSAGE                     = "message";
	private static final String             TOPIC                       = "topic";
	private DefaultWebSwallowMessageDAO       	smdi;
	private volatile List<String>           dbNames 					= new ArrayList<String>(); 
	
	private Map<String, MongoClient> 		topicNameToMongoMap 		= new HashMap<String, MongoClient>();
	private List<MongoClient>               allReadMongo 				= new ArrayList<MongoClient>();
	private MongoOperations 				readMongoOps;
	private Long 							totalNumOfTopic 			= new Long(0);
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
	

	//doing all query, so use readMongoOps
	@RequestMapping(value = "/message/messagedefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object messageDefault(String offset, String limit, String tname, String messageId,
			String startdt, String stopdt) throws UnknownHostException {
		
		topicNameToMongoMap = MongoManager.getInstance().getTopicNameToMongoMap();  //name starts without msg#
		allReadMongo = MongoManager.getInstance().getAllReadMongo();
		
		for(MongoClient mc: allReadMongo){ 
			dbNames.addAll(mc.getDatabaseNames());
		}
		
		
		int start = Integer.parseInt(offset);
		int span = Integer.parseInt(limit); // get span+1 topics so that it can
		Map<String, Object> map = new HashMap<String, Object>();
		if((tname + messageId).isEmpty()){
			map = getMessageFromFirstTopic(start, span);
		}
		else{
			if(!tname.isEmpty()){
				map = getMessageFromSpecificTopic(start, span, tname, messageId ,startdt, stopdt);
			}
		}
		Gson gson = new Gson(); // for last page to return
		return gson.toJson(map);
						
	}
	
	private Map<String, Object> getMessageFromSpecificTopic(int start, int span,String  tname, String messageId
			,String startdt, String stopdt){
		String dbn = PRE_MSG + tname;
		long mid = 0;
		if(!messageId.isEmpty()){  //messageId is not empty
			if(isIP(messageId)){ //query based on IP
				return getByIp(dbn, start, span, messageId);
			}
			else{
				try{
					mid = Long.parseLong(messageId.trim());
				}catch(NumberFormatException e){
					mid = 0;
				}
			}
		}
		return getResults(dbn,start,span,mid, startdt, stopdt); 
	}
	
	private MongoClient getMongoFromMap(String dbn){
		if(topicNameToMongoMap.get(dbn) == null)
			return topicNameToMongoMap.get(DEFAULT);
		return topicNameToMongoMap.get(DEFAULT);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getByIp(String dbn, int start, int span,String ip){
		String subStr = dbn.substring(PRE_MSG.length());
		readMongoOps = new MongoTemplate(new SimMongoDbFactory(getMongoFromMap(subStr), dbn)); //write in writeMongo
		smdi = new DefaultWebSwallowMessageDAO(readMongoOps);
		Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
		List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
		sizeAndMessage  = smdi.findByIp(start, span, ip);
		totalNumOfTopic = (Long) sizeAndMessage.get(SIZE);
		messageList = (List<WebSwallowMessage>) sizeAndMessage.get(MESSAGE);
		for(WebSwallowMessage m : messageList)
			setSMessageProperty(m);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SIZE, totalNumOfTopic);
		map.put(TOPIC, messageList);
		return map;
	}
	
	//read use readMongoOps
	@SuppressWarnings("unchecked")
	private Map<String, Object> getResults(String dbn,int start,  int span, long mid, String startdt, String stopdt){
		String subStr = dbn.substring(PRE_MSG.length());
		readMongoOps = new MongoTemplate(new SimMongoDbFactory(getMongoFromMap(subStr), dbn)); //write in writeMongo
		smdi = new DefaultWebSwallowMessageDAO(readMongoOps);
		List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
		if(startdt == null || startdt.isEmpty() ){
			messageList = smdi.findSpecific(start, span, mid);
			if(mid == 0){
				totalNumOfTopic = smdi.count();  //set size
			}
			else
				totalNumOfTopic = (long) messageList.size();
		}
		else{
			Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
			sizeAndMessage  = smdi.findByTime(start, span, startdt, stopdt);
			totalNumOfTopic = (Long) sizeAndMessage.get(SIZE);
			messageList = (List<WebSwallowMessage>) sizeAndMessage.get(MESSAGE);
		}

		for(WebSwallowMessage m : messageList)
			setSMessageProperty(m);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SIZE, totalNumOfTopic);
		map.put(TOPIC, messageList);
		return map;
	}
	
	public Map<String, Object> getMessageFromFirstTopic(int start, int span)
			throws UnknownHostException {

		Map<String, Object> map = new HashMap<String, Object>();
		String  KEY = SIZE;
		for (String dbn : dbNames) {
			if (dbn != null && dbn.startsWith(PRE_MSG)) {
				map = getResults(dbn, start, span, 0, null, null);
				int size = Integer.parseInt(String.valueOf(map.get(KEY))); 
				if(size == 0)
					continue;
				else
					return map;
				}
			}
		//no message
		return map;
	}
	
	private void setSMessageProperty(WebSwallowMessage m){
		m.setMid(m.getId());
		m.setGtstring(m.getGt());
		m.setStstring(m.getId());
	}
	
	private boolean isIP(String str){
		String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.find();
	}
	
}

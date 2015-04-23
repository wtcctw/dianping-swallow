package com.dianping.swallow.web.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.dianping.swallow.web.model.WebSwallowMessage;
import com.dianping.swallow.web.util.MongoUtils;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 * 2015年4月20日 下午9:31:41
 */
public class WebSwallowMessageDAOImpl implements WebSwallowMessageDAO {
 
    private MongoOperations mongoOps;
    //集合名字
    private static final String MESSAGE_COLLECTION                			 = "c";
    private static final String TIMEFORMAT                                   = "yyyy-MM-dd HH:mm";
    private static final String ID                                           = "_id";
    private static final String GT                                           = "gt";
    private static final String SIZE                                         = "size";
    private static final String MESSAGE                                      = "message";
    private static final String SI                                           = "si";    
    public WebSwallowMessageDAOImpl(MongoOperations mongoOps){
        this.mongoOps=mongoOps;
    }
     
    @Override
    public void create(WebSwallowMessage p) {
        this.mongoOps.insert(p, MESSAGE_COLLECTION);
    }
 
    @Override
    public WebSwallowMessage readById(String id) {
        Query query = new Query(Criteria.where(ID).is(id));
        return this.mongoOps.findOne(query, WebSwallowMessage.class, MESSAGE_COLLECTION);
    }
 
    @Override
    public void update(WebSwallowMessage p) {
        this.mongoOps.save(p, MESSAGE_COLLECTION);
    }
 
    @Override
    public int deleteById(String id) {
        Query query = new Query(Criteria.where(ID).is(id));
        WriteResult result = this.mongoOps.remove(query, WebSwallowMessage.class, MESSAGE_COLLECTION);
        return result.getN();
    }
    
    @Override
	public long count(){
    	Query query = new Query();
    	return this.mongoOps.count(query, MESSAGE_COLLECTION);
    }
	
  
    @Override
    public List<WebSwallowMessage> findSpecific(int offset, int limit, long mid){
    	Query query = new Query();
    	if(mid == 0){
    		query.with(new Sort(new Sort.Order(Direction.DESC, ID)));  //降序
    	}
    	else{
    		//query = new Query(Criteria.where(ID).is(MongoUtils.longToBSONTimestamp(mid)));
    		return findSpecificWithId(offset, limit, mid);
    	}
        query.skip(offset).limit(limit);
        return this.mongoOps.find(query, WebSwallowMessage.class, MESSAGE_COLLECTION);
    }
    
    @Override
    public Map<String, Object>  findByIp(int offset, int limit, String ip){
    	List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	Query query1 = new Query(Criteria.where(SI).is(ip));
    	query1.skip(offset).limit(limit);
    	messageList = this.mongoOps.find(query1, WebSwallowMessage.class, MESSAGE_COLLECTION);
    	Query query2 = new Query(Criteria.where(SI).is(ip));
       	long size = this.mongoOps.count(query2, WebSwallowMessage.class, MESSAGE_COLLECTION);
    	map.put(SIZE, size);
    	map.put(MESSAGE, messageList);
        return map;
    }
    
    @Override
    public Map<String, Object>  findByTime(int offset, int limit, String startdt, String stopdt){
    	SimpleDateFormat sdf = new SimpleDateFormat(TIMEFORMAT);
    	Query query1 = new Query();
    	Query query2 = new Query();
    	Long size = 0L;
    	List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	try {
			Date datestart = addEightHours(sdf.parse(startdt));
			Date datestop = addEightHours(sdf.parse(stopdt));
	
			query1.addCriteria(Criteria.where(GT).exists(true).andOperator(
					Criteria.where(GT).gte(datestart),
					Criteria.where(GT).lte(datestop) ));
			size = this.mongoOps.count(query1, WebSwallowMessage.class, MESSAGE_COLLECTION);
			
			query2.addCriteria(Criteria.where(GT).exists(true).andOperator(
					Criteria.where(GT).gte(datestart),
					Criteria.where(GT).lte(datestop) ));
			query2.skip(offset).limit(limit);
			messageList = this.mongoOps.find(query2, WebSwallowMessage.class, MESSAGE_COLLECTION);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	map.put(SIZE, size);
    	map.put(MESSAGE, messageList);
        return map;
    }
    

    private Date addEightHours(Date date){
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.add(Calendar.MINUTE, 0 * 60);
    	return c.getTime();
    }
    
    private List<WebSwallowMessage> findSpecificWithId(int offset, int limit, long mid){
    	List<WebSwallowMessage> tmpList = this.mongoOps.findAll(WebSwallowMessage.class, MESSAGE_COLLECTION);
    	List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
    	for(WebSwallowMessage sm : tmpList){
    		Long searchmid = MongoUtils.BSONTimestampToLong(sm.getId());
    		if(searchmid.equals(new Long(mid)))  //use equals not ==
    			messageList.add(sm);
    	}
    	
    	return messageList;  // one message
    }
 
}
package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.dianping.swallow.web.dao.SearchPropDAO;
import com.dianping.swallow.web.model.SearchProp;

/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:04:41
 */
public class DefaultSearchPropDAO  implements SearchPropDAO{
	
	private  String DEPARTMENT_COLLECTION;
	private  String NAME;                                                                      
	
	private MongoOperations mongoOps;
	 
	public DefaultSearchPropDAO(MongoOperations mongoOps, String col, String name){
	    this.mongoOps=mongoOps;
	    this.DEPARTMENT_COLLECTION = col;
	    this.NAME = name;
}
	
	@Override
	public void create(SearchProp p){
		this.mongoOps.insert(p, DEPARTMENT_COLLECTION);
	}

    @Override
	public SearchProp readByDept(String dept) {
        Query query = new Query(Criteria.where(NAME).is(dept));
        return this.mongoOps.findOne(query, SearchProp.class, DEPARTMENT_COLLECTION);
    }

    @Override
	public void dropCol(){
        	this.mongoOps.dropCollection(DEPARTMENT_COLLECTION);
    }
	
    @Override
	public List<SearchProp> findAll(){
    	return this.mongoOps.findAll(SearchProp.class, DEPARTMENT_COLLECTION);
    }

}

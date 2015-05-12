package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.model.Administrator;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 * 2015年5月11日 上午12:00:31
 */
public class DefaultAdministratorDao extends AbstractWriteDao implements AdministratorDao{
	
    //collection name
    private static final String 			ADMINISTRATOR_COLLECTION 						= "swallowwebadminc";
    private static final String 			VISIT_COLLECTION 								= "swallowwebvisitc";
    private static final String 			EMAIL 											= "email";
	
    @Override
    public Administrator readByEmail(String email){
        Query query = new Query(Criteria.where(EMAIL).is(email));
        return mongoTemplate.findOne(query, Administrator.class, ADMINISTRATOR_COLLECTION);
    }

    @Override
	public void createAdministrator(Administrator a){
		mongoTemplate.insert(a, ADMINISTRATOR_COLLECTION);
	}
	
	@Override
	public void saveAdministrator(Administrator a) {
		mongoTemplate.save(a, ADMINISTRATOR_COLLECTION);
	}
	
	@Override
	public int deleteByEmail(String email){
        Query query = new Query(Criteria.where(EMAIL).is(email));
        WriteResult result = mongoTemplate.remove(query, Administrator.class, ADMINISTRATOR_COLLECTION);
        return result.getN();
	}
	
	@Override
	public void dropCol(){
		mongoTemplate.dropCollection(ADMINISTRATOR_COLLECTION);
	}
	
	@Override
	public List<Administrator> findAll(){
		return mongoTemplate.findAll(Administrator.class, ADMINISTRATOR_COLLECTION);
	}
	
	@Override
	public long countAdministrator(){
    	Query query = new Query();
    	return mongoTemplate.count(query, ADMINISTRATOR_COLLECTION);
	}
	
	@Override
	public List<Administrator> findFixedAdministrator(int offset, int limit){
        Query query = new Query();  
        query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, EMAIL))); //根据email字段排序
        return mongoTemplate.find(query, Administrator.class, ADMINISTRATOR_COLLECTION);
	}

	@Override
	public void saveVisit(Administrator v) {
		mongoTemplate.save(v, VISIT_COLLECTION);
		
	}

	@Override
	public Administrator readByVisitEmail(String email) {
        Query query = new Query(Criteria.where(EMAIL).is(email));
        return mongoTemplate.findOne(query, Administrator.class, VISIT_COLLECTION);
	}
	
	@Override
	public List<Administrator> findAllVisit(){
		return mongoTemplate.findAll(Administrator.class, VISIT_COLLECTION);
	}

}

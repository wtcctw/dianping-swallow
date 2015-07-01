package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.model.Administrator;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年5月11日 上午12:00:31
 */
@Component
public class DefaultAdministratorDao extends AbstractWriteDao implements
		AdministratorDao {

	private static final String ADMINISTRATOR_COLLECTION = "swallowwebadminc";
	private static final String NAME = "name";
	private static final String ROLE = "role";
	private static final String DATE = "date";

	@Override
	public Administrator readByName(String name) {
		Query query = new Query(Criteria.where(NAME).is(name));
		return mongoTemplate.findOne(query, Administrator.class,
				ADMINISTRATOR_COLLECTION);
	}

	@Override
	public boolean createAdministrator(Administrator a) {
		try {
			mongoTemplate.insert(a, ADMINISTRATOR_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save " + a, e);
		}
		return false;
	}

	@Override
	public boolean saveAdministrator(Administrator a) {
		try {
			mongoTemplate.save(a, ADMINISTRATOR_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save " + a, e);
		}
		return false;
	}

	@Override
	public int deleteByName(String name) {
		Query query = new Query(Criteria.where(NAME).is(name));
		WriteResult result = mongoTemplate.remove(query, Administrator.class,
				ADMINISTRATOR_COLLECTION);
		return result.getN();
	}

	@Override
	public void dropCol() {
		mongoTemplate.dropCollection(ADMINISTRATOR_COLLECTION);
	}

	@Override
	public List<Administrator> findAll() {
		return mongoTemplate.findAll(Administrator.class,
				ADMINISTRATOR_COLLECTION);
	}

	@Override
	public long countAdministrator() {
		Query query = new Query();
		return mongoTemplate.count(query, ADMINISTRATOR_COLLECTION);
	}

	@Override
	public List<Administrator> findFixedAdministrator(int offset, int limit) {
		Query query = new Query();
		query.skip(offset)
				.limit(limit)
				.with(new Sort(new Sort.Order(Direction.ASC, ROLE),
						new Sort.Order(Direction.DESC, DATE))); // 根据role and
																// date字段排序
		return mongoTemplate.find(query, Administrator.class,
				ADMINISTRATOR_COLLECTION);
	}

}

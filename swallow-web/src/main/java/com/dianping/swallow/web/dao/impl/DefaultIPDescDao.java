package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.IPDescDao;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.mongodb.WriteResult;

/**
 * 
 * @author qi.yin
 *
 */
@Service("ipDescDao")
public class DefaultIPDescDao extends AbstractWriteDao implements IPDescDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultIPDescDao.class);

	private static final String IPDESC_COLLECTION = "swallowwebipdescc";

	private static final String IP_FIELD = "ip";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(IPDesc ipDesc) {
		try {
			mongoTemplate.save(ipDesc, IPDESC_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + ipDesc, e);
		}
		return false;
	}

	@Override
	public boolean update(IPDesc ipDesc) {
		return insert(ipDesc);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, IPDesc.class, IPDESC_COLLECTION);
		return result.getN();
	}

	@Override
	public int deleteByIp(String ip) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip));
		WriteResult result = mongoTemplate.remove(query, IPDesc.class, IPDESC_COLLECTION);
		return result.getN();
	}

	@Override
	public IPDesc findByIp(String ip) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip));
		IPDesc ipDesc = mongoTemplate.findOne(query, IPDesc.class, IPDESC_COLLECTION);
		return ipDesc;
	}

	@Override
	public IPDesc findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		IPDesc ipDesc = mongoTemplate.findOne(query, IPDesc.class, IPDESC_COLLECTION);
		return ipDesc;
	}

	@Override
	public List<IPDesc> findAll() {
		return mongoTemplate.findAll(IPDesc.class, IPDESC_COLLECTION);
	}

}

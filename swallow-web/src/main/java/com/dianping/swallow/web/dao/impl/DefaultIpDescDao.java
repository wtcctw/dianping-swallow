package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.dianping.swallow.web.dao.IpDescDao;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.model.cmdb.IpDesc;
import com.mongodb.WriteResult;

/**
 * 
 * @author qi.yin
 *
 */
public class DefaultIpDescDao extends AbstractWriteDao implements IpDescDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultIpDescDao.class);

	private static final String IPDESC_COLLECTION = "swallowwebipdescc";

	private static final String IP_FIELD = "ip";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(IpDesc ipDesc) {
		try {
			mongoTemplate.save(ipDesc, IPDESC_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + ipDesc, e);
		}
		return false;
	}

	@Override
	public boolean update(IpDesc ipDesc) {
		return insert(ipDesc);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, IpDesc.class, IPDESC_COLLECTION);
		return result.getN();
	}

	@Override
	public int deleteByIp(String ip) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip));
		WriteResult result = mongoTemplate.remove(query, IpDesc.class, IPDESC_COLLECTION);
		return result.getN();
	}

	@Override
	public IpDesc findByIp(String ip) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip));
		IpDesc ipDesc = mongoTemplate.findOne(query, IpDesc.class, IPDESC_COLLECTION);
		return ipDesc;
	}

	@Override
	public IpDesc findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		IpDesc ipDesc = mongoTemplate.findOne(query, IpDesc.class, IPDESC_COLLECTION);
		return ipDesc;
	}

	@Override
	public List<IpDesc> findAll() {
		return mongoTemplate.findAll(IpDesc.class, IPDESC_COLLECTION);
	}

}

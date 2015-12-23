package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.web.model.dom.MongoReport;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.service.GroupResourceService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.service.MongoResourceService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mingdongli
 *
 *         2015年9月18日上午9:58:52
 */
@Component
public class MongoResourceCollector extends AbstractRegularCollecter {

	private static final String MONGO_REPORT = "http://dom.dp/db_daily/message";

	private static final String MONGO_IP_MAPPING = "http://tools.dba.dp/get_mongo_ips.php";

	private static final String DATA = "data";

	private static final String STATUS = "status";

	private static final String MESSAGE = "message";

	private static final String GENETAL = "一般消息队列";

	@Resource(name = "mongoResourceService")
	private MongoResourceService mongoResourceService;

	@Resource(name = "groupResourceService")
	private GroupResourceService groupResourceService;

	@Autowired
	private HttpService httpSerivice;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		collectorName = getClass().getSimpleName();
		collectorInterval = 6 * 60;
		collectorDelay = 1;
	}

	@Override
	public void doCollector() {

		if(!EnvUtil.isProduct()){
			return;
		}
		if(logger.isInfoEnabled()){
			logger.info("[doCollector] start collect mongoResource.");
		}
		HttpResult httpResult = httpSerivice.httpPost(MONGO_REPORT, new ArrayList<NameValuePair>());

		if (httpResult.isSuccess()) {
			String response = httpResult.getResponseBody();
			Map<String, List<String>> catalogToIp = loadIpMapping();
			ObjectMapper mapper = new ObjectMapper();
			JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, String.class, List.class);
			try {
				Map<String, List<Map<String, Object>>> map = mapper.readValue(response, javaType);
				List<Map<String, Object>> datas = map.get(DATA);

				for (Map<String, Object> data : datas) {
					MongoReport mongoReport = convertMapToObject(data);
					MongoResource mongoResource = convertToMongoResource(mongoReport);
					String catalog = mongoResource.getCatalog();
					if (catalogToIp != null) {
						List<String> ipList = catalogToIp.get(catalog);
						if (ipList != null && ipList.size() > 1) {
							String ips = StringUtils.join(ipList, ",");
							mongoResource.setIp(ips);
							MongoResource mongoResourceOld = mongoResourceService.findByIp(ips);
							if (mongoResourceOld != null) {
								String mongoType = mongoResourceOld.getGroupName();
								if (mongoType != null) {
									mongoResource.setGroupName(mongoType);
								}
								mongoResource.setId(mongoResourceOld.getId());
							}
							mongoResourceService.update(mongoResource);
						}
					}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error when parse response to MongoReport");
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Error when post mongo report");
			}
		}
	}

	private Map<String, List<String>> loadIpMapping() {

		HttpResult httpResult = httpSerivice.httpGet(MONGO_IP_MAPPING);

		if (httpResult.isSuccess()) {
			String responseBody = httpResult.getResponseBody();
			Map<String, List<String>> map = extractIpFromHttpResult(responseBody);
			return map;
		}
		return null;
	}

	private Map<String, List<String>> extractIpFromHttpResult(String mapping) {

		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
		try {
			Map<String, Object> map = mapper.readValue(mapping, javaType);
			Integer status = (Integer) map.get(STATUS);
			if (status != null && status == 0) {
				@SuppressWarnings("unchecked")
				Map<String, List<String>> messages = (Map<String, List<String>>) map.get(MESSAGE);
				return messages;
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error when readValue");
			}
		}

		return null;

	}

	private MongoResource convertToMongoResource(MongoReport mongoReport) {

		MongoResource mongoResource = new MongoResource();
		String catalog = mongoReport.getCatalog();
		String mongoType = GENETAL;
		List<String> groupNames = groupResourceService.findAllGroupName();

		for(String gn : groupNames){
			if(gn != null && gn.startsWith(catalog)){
				mongoType = gn;
			}
		}

		mongoResource.setGroupName(mongoType);
		mongoResource.setCatalog(catalog);
		mongoResource.setDisk(mongoReport.getDisk());
		mongoResource.setLoad(mongoReport.getLoad());
		mongoResource.setQps(mongoReport.getQps());

		return mongoResource;
	}

	private MongoReport convertMapToObject(Map<String, Object> data) {

		ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
		MongoReport pojo = mapper.convertValue(data, MongoReport.class);
		return pojo;
	}

	@Override
	public int getCollectorDelay() {

		return collectorDelay;
	}

	@Override
	public int getCollectorInterval() {

		return collectorInterval;
	}

	public void setMongoResourceService(MongoResourceService mongoResourceService) {
		this.mongoResourceService = mongoResourceService;
	}

	public void setHttpSerivice(HttpService httpSerivice) {
		this.httpSerivice = httpSerivice;
	}
}

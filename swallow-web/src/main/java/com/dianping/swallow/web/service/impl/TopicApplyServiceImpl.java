package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.dom.MongoReport;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.service.LionHttpService;
import com.dianping.swallow.web.service.TopicApplyService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.DateUtil;
import com.dianping.swallow.web.util.ResponseStatus;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * @author mingdongli
 *
 *         2015年9月16日下午6:59:12
 */
@Service("topicApplyService")
public class TopicApplyServiceImpl extends AbstractSwallowService implements TopicApplyService, Runnable {

	private static final String MONGO_REPORT = "http://dom.dp/db_daily/message";

	private static final String MONGO_IP_MAPPING = "http://tools.dba.dp/get_mongo_ips.php";

	private static final String FACTORY_NAME = "TopicApply";

	private static final String DATA = "data";

	private static final String STATUS = "status";

	private static final String MESSAGE = "message";

	private static final String BLANK_STRING = "";

	private static final String PAY_MONGO = "下单消息队列";

	private static final String SEARCH_MONGO = "搜索消息队列";

	private static final int BASE_QPX = 5000;

	private static final int MAX_QPX = 7000;

	private static final int MASTER_PORT = 8081;

	private static final int SLAVE_PORT = 8082;

	private String bestMongo;

	private String searchMongo;

	private String bestConsumerServer;

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Resource(name = "lionHttpService")
	private LionHttpService lionHttpService;

	@Resource(name = "consumerServerStatsDataService")
	private ConsumerServerStatsDataService consumerServerStatsDataService;

	@Autowired
	private HttpService httpSerivice;

	@Autowired
	private UserUtils userUtils;

	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(
			CommonUtils.DEFAULT_CPU_COUNT, ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	@PostConstruct
	void updateDashboardContainer() {

		logger.info("[PerformanceIndexCollector]");
		scheduledExecutorService.scheduleAtFixedRate(this, 0, 6, TimeUnit.HOURS);
	}

	@Override
	public Pair<String, ResponseStatus> chooseSearchMongoDb() {

		String searchCatalog = null;
		Pair<String, ResponseStatus> pair = new Pair<String, ResponseStatus>();
		HttpResult httpResult = httpSerivice.httpPost(MONGO_REPORT, new ArrayList<NameValuePair>());

		if (httpResult.isSuccess()) {
			String response = httpResult.getResponseBody();
			ObjectMapper mapper = new ObjectMapper();
			JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, String.class, List.class);

			try {
				Map<String, List<Map<String, Object>>> map = mapper.readValue(response, javaType);
				List<Map<String, Object>> datas = map.get(DATA);

				for (Map<String, Object> data : datas) {
					MongoReport mongoReport = convertMapToObject(data);
					String catalog = mongoReport.getCatalog();
					if (SEARCH_MONGO.equals(catalog)) {
						if (logger.isInfoEnabled()) {
							logger.info(mongoReport.toString());
						}
						searchCatalog = catalog;
						break;
					}
				}
				if (StringUtils.isBlank(searchCatalog)) {
					pair.setSecond(ResponseStatus.NOTEXIST);
					return pair;
				}

				httpResult = httpSerivice.httpGet(MONGO_IP_MAPPING);

				if (httpResult.isSuccess()) {
					String responseBody = httpResult.getResponseBody();
					String searchMongo = extractIpFromHttpResult(responseBody, searchCatalog);

					if (StringUtils.isNotBlank(searchMongo)) {
						pair.setFirst(searchMongo);
						pair.setSecond(ResponseStatus.SUCCESS);
						return pair;
					} else {
						pair.setSecond(ResponseStatus.INVALIDIP);
						return pair;
					}

				} else {
					if (logger.isErrorEnabled()) {
						logger.error("Error when get ip mapping");
					}
					pair.setSecond(ResponseStatus.HTTPEXCEPTION);
					return pair;
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error when parse response to MongoReport");
				}
				pair.setSecond(ResponseStatus.RUNTIMEEXCEPTION);
				return pair;
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Error when post mongo report");
			}
			pair.setSecond(ResponseStatus.HTTPEXCEPTION);
			return pair;
		}

	}

	@Override
	public Pair<String, ResponseStatus> chooseMongoDbWithoutSearch() {

		Pair<String, ResponseStatus> pair = new Pair<String, ResponseStatus>();
		HttpResult httpResult = httpSerivice.httpPost(MONGO_REPORT, new ArrayList<NameValuePair>());

		if (httpResult.isSuccess()) {
			String response = httpResult.getResponseBody();
			ObjectMapper mapper = new ObjectMapper();
			JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, String.class, List.class);

			try {
				Map<String, List<Map<String, Object>>> map = mapper.readValue(response, javaType);
				List<Map<String, Object>> datas = map.get(DATA);
				Set<MongoReport> mongoReportSet = new TreeSet<MongoReport>();

				for (Map<String, Object> data : datas) {
					MongoReport mongoReport = convertMapToObject(data);
					Float disk = mongoReport.getDisk();
					String catalog = mongoReport.getCatalog();
					if (disk != null && disk <= 80f && !PAY_MONGO.equals(catalog) && !SEARCH_MONGO.equals(catalog)) {
						if (logger.isInfoEnabled()) {
							logger.info(mongoReport.toString());
						}
						mongoReportSet.add(mongoReport);
					}
				}
				if (mongoReportSet.isEmpty()) {
					pair.setSecond(ResponseStatus.NODISKSPACE);
					return pair;
				}
				pair = doChooseBestMongo(mongoReportSet);

				if (pair.getSecond() == ResponseStatus.SUCCESS) {
					httpResult = httpSerivice.httpGet(MONGO_IP_MAPPING);

					if (httpResult.isSuccess()) {
						String responseBody = httpResult.getResponseBody();
						String ip = extractIpFromHttpResult(responseBody, pair.getFirst());

						if (StringUtils.isNotBlank(ip)) {
							pair.setFirst(ip);
							return pair;
						} else {
							pair.setSecond(ResponseStatus.INVALIDIP);
							return pair;
						}

					} else {
						if (logger.isErrorEnabled()) {
							logger.error("Error when get ip mapping");
						}
						pair.setSecond(ResponseStatus.HTTPEXCEPTION);
						return pair;
					}

				} else {
					return pair;
				}

			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error when parse response to MongoReport");
				}
				pair.setSecond(ResponseStatus.RUNTIMEEXCEPTION);
				return pair;
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Error when post mongo report");
			}
			pair.setSecond(ResponseStatus.HTTPEXCEPTION);
			return pair;
		}

	}

	private Pair<String, ResponseStatus> doChooseBestMongo(Set<MongoReport> set) {

		Pair<String, ResponseStatus> pair = new Pair<String, ResponseStatus>();
		int baseQps = BASE_QPX;

		while (baseQps <= MAX_QPX) {
			for (MongoReport mongoReport : set) {
				Integer qps = mongoReport.getQps();

				if (qps != null && qps <= baseQps) {
					String catalog = mongoReport.getCatalog();
					pair.setSecond(ResponseStatus.SUCCESS);
					pair.setFirst(catalog);
					return pair;
				}
			}
			baseQps += 1000;
		}

		pair.setSecond(ResponseStatus.TOOLARGEQPS);
		return pair;
	}

	private String extractIpFromHttpResult(String mapping, String key) {

		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
		try {
			Map<String, Object> map = mapper.readValue(mapping, javaType);
			Integer status = (Integer) map.get(STATUS);
			if (status != null && status == 0) {
				@SuppressWarnings("unchecked")
				Map<String, List<String>> messages = (Map<String, List<String>>) map.get(MESSAGE);
				List<String> ips = messages.get(key);
				if (ips != null && ips.size() == 2) {
					return StringUtils.join(ips, ",");
				} else {
					return BLANK_STRING;
				}
			} else {
				return BLANK_STRING;
			}
		} catch (Exception e) {
			return BLANK_STRING;
		}

	}

	private MongoReport convertMapToObject(Map<String, Object> data) {

		ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
		MongoReport pojo = mapper.convertValue(data, MongoReport.class);
		return pojo;
	}

	@Override
	public Pair<String, ResponseStatus> chooseConsumerServer() {

		Set<String> masters = new HashSet<String>();
		Map<String, String> master2slave = new HashMap<String, String>();

		Map<String, Pair<String, String>> topicToConsumerServer = topicResourceService
				.loadCachedTopicToConsumerServer();
		for (Map.Entry<String, Pair<String, String>> entry : topicToConsumerServer.entrySet()) {
			String master = entry.getValue().getFirst();
			String slave = entry.getValue().getSecond();
			masters.add(master);
			master2slave.put(master, slave);
		}

		long originalStart = DateUtil.getYesterayStart();
		long originalStop = DateUtil.getYesterayStop();
		long startKey = AbstractRetriever.getKey(originalStart);
		long endKey = AbstractRetriever.getKey(originalStop);

		int count = 0;
		while (count < 5) {
			String bestMaster = consumerServerStatsDataService.findIdleConsumerServer(new ArrayList<String>(masters),
					startKey, endKey);

			if (StringUtils.isBlank(bestMaster)) {
				count++;
				originalStart = DateUtil.getOneDayBefore(originalStart);
				originalStop = DateUtil.getOneDayBefore(originalStop);
				startKey = AbstractRetriever.getKey(originalStart);
				endKey = AbstractRetriever.getKey(originalStop);
			} else {
				String slaveIp = master2slave.get(bestMaster);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(bestMaster).append(":").append(MASTER_PORT).append(",").append(slaveIp)
						.append(":").append(SLAVE_PORT);
				return new Pair<String, ResponseStatus>(stringBuilder.toString(), ResponseStatus.SUCCESS);
			}
		}

		return new Pair<String, ResponseStatus>(BLANK_STRING, ResponseStatus.NOCONSUMERSERVER);
	}

	@Override
	public String getBestMongo() {
		return bestMongo;
	}

	@Override
	public String getSearchMongo() {
		return searchMongo;
	}

	@Override
	public String getBestConsumerServer() {
		return bestConsumerServer;
	}

	@Override
	public void run() {

		try {
			SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(),
					"updateMongoAndConsumerServer");
			catWrapper.doAction(new SwallowAction() {
				@Override
				public void doAction() throws SwallowException {

					Pair<String, ResponseStatus> pair = chooseMongoDbWithoutSearch();
					if (pair.getSecond() == ResponseStatus.SUCCESS) {
						bestMongo = pair.getFirst();
					}

					pair = chooseSearchMongoDb();
					if (pair.getSecond() == ResponseStatus.SUCCESS) {
						searchMongo = pair.getFirst();
					}

					pair = chooseConsumerServer();
					if (pair.getSecond() == ResponseStatus.SUCCESS) {
						bestConsumerServer = pair.getFirst();
					}

				}
			});
		} catch (Throwable th) {
			logger.error("[startPerformanceIndexCollector]", th);
		} finally {

		}

	}

}

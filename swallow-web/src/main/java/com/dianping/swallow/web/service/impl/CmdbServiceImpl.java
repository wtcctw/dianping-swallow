package com.dianping.swallow.web.service.impl;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.service.IPDescService;

/**
 * 
 * @author qiyin
 *
 */
@Service("cmdbService")
public class CmdbServiceImpl implements CmdbService {

	private static final Logger logger = LoggerFactory.getLogger(CmdbServiceImpl.class);

	private static final String CMDB_API_FILE = "cmdb-api-url.properties";

	private static final String URLBYIP_KEY = "urlByIp";

	private String urlByIpFormat;

	@Autowired
	private HttpService httpService;

	@Autowired
	private IPDescService ipDescService;

	private ObjectMapper objectMapper;

	public CmdbServiceImpl() {
		setObjectMapper(new ObjectMapper());
		try {
			InputStream in = CmdbServiceImpl.class.getClassLoader().getResourceAsStream(CMDB_API_FILE);
			if (in != null) {
				if (logger.isInfoEnabled()) {
					logger.info("loading " + CMDB_API_FILE);
				}
				Properties prop = new Properties();
				try {
					prop.load(in);
					urlByIpFormat = StringUtils.trim(prop.getProperty(URLBYIP_KEY));
				} finally {
					in.close();
				}

			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	public IPDesc getIpDesc(String ip) {
		String url = getRealUrl(ip);
		HttpResult result = httpService.httpGet(url);
		if (!result.isSuccess()) {
			return null;
		}
		String content = result.getResponseBody();
		IPDesc ipDesc = null;
		try {
			ipDesc = transformToIpDesc(content);
			ipDesc.setIp(ip);
		} catch (Exception e) {
			logger.info("json transform to object failed .", e);
		}
		return ipDesc;
	}

	private String getRealUrl(String ip) {
		return StringUtils.replace(urlByIpFormat, "{ip}", ip);
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	private IPDesc transformToIpDesc(String content) throws Exception {
		JsonNode rootNode = objectMapper.readTree(content);
		JsonNode projectsNode = rootNode.path("projects");
		JsonNode jsonNode = projectsNode.get(0);
		IPDesc ipDesc = null;
		if (jsonNode != null) {
			ipDesc = new IPDesc();
			ipDesc.setName(jsonNode.path("project_name").getTextValue());
			ipDesc.setEmail(jsonNode.path("project_email").getTextValue());
			ipDesc.setOpEmail(jsonNode.path("op_email").getTextValue());
			ipDesc.setOpManager(jsonNode.path("op_duty").getTextValue());
			ipDesc.setOpMobile(jsonNode.path("op_mobile").getTextValue());
			ipDesc.setDpManager(jsonNode.path("rd_duty").getTextValue());
			ipDesc.setDpMobile(jsonNode.path("rd_mobile").getTextValue());
			return ipDesc;
		}
		return ipDesc;
	}
}

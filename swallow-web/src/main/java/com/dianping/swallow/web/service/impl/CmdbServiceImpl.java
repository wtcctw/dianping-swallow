package com.dianping.swallow.web.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.swallow.web.model.cmdb.EnvDevice;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

/**
 * 
 * @author qiyin
 *
 */
@Service("cmdbService")
public class CmdbServiceImpl implements CmdbService, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(CmdbServiceImpl.class);

	private static final String CMDB_API_URL_FILE = "cmdb-api-url.properties";

	private static final String PROJECT_URL_KEY = "projectUrl";

	private static final String DEVICE_URL_KEY = "deviceUrl";

	private static final String env;

	static {
		env = EnvZooKeeperConfig.getEnv().trim();
	}

	private String projectUrlFormat;

	private String deviceUrlFormat;

	@Autowired
	private HttpService httpService;

	private ObjectMapper objectMapper;

	public CmdbServiceImpl() {
		objectMapper = new ObjectMapper();
	}

	@Override
	public IPDesc getIpDesc(String ip) {
		String url = getRealUrl(projectUrlFormat, "{ip}", ip);
		HttpResult result = httpService.httpGet(url);
		if (!result.isSuccess()) {
			return null;
		}
		String content = result.getResponseBody();
		IPDesc ipDesc = null;
		try {
			ipDesc = transformToIpDesc(content);
			if (ipDesc == null) {
				return null;
			}
			ipDesc.setIp(ip);
		} catch (Exception e) {
			logger.info("json transform to object failed .", e);
		}
		return ipDesc;
	}

	@Override
	public List<EnvDevice> getEnvDevices(String project) {
		String url = getRealUrl(deviceUrlFormat, "{project}", project);
		String env = transformEnv();
		url = getRealUrl(url, "{env}", env);
		url = getRealUrl(url, "{page}", String.valueOf(1));
		HttpResult result = httpService.httpGet(url);
		if (!result.isSuccess()) {
			return null;
		}
		String content = result.getResponseBody();
		List<EnvDevice> envDevices = null;
		try {
			envDevices = transformToEnvDevices(content);
		} catch (Exception e) {
			logger.info("json transform to object failed .", e);
		}
		return envDevices;
	}

	private String getRealUrl(String format, String key, String value) {
		return StringUtils.replace(format, key, value);
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

	private List<EnvDevice> transformToEnvDevices(String content) throws Exception {
		JsonNode rootNode = objectMapper.readTree(content);
		JsonNode totalNode = rootNode.path("total");
		int num = totalNode.getIntValue();
		JsonNode devicesNode = rootNode.path("devices");
		List<EnvDevice> envDevices = new ArrayList<EnvDevice>();
		for (int i = 0; i < num; i++) {
			JsonNode deviceNode = devicesNode.get(i);
			EnvDevice envDevice = null;
			if (deviceNode != null) {
				envDevice = new EnvDevice();
				envDevice.setHostName(deviceNode.path("hostname").getTextValue());
				envDevice.setEnv(env);
				JsonNode ipNode = deviceNode.path("private_ip");
				if (ipNode.get(0) != null) {
					envDevice.setIp(ipNode.get(0).getTextValue());
				}
				envDevices.add(envDevice);
			}
		}
		return envDevices;
	}

	private void initProperties() {
		try {
			InputStream in = CmdbServiceImpl.class.getClassLoader().getResourceAsStream(CMDB_API_URL_FILE);
			if (in != null) {
				Properties prop = new Properties();
				try {
					prop.load(in);
					projectUrlFormat = StringUtils.trim(prop.getProperty(PROJECT_URL_KEY));
					deviceUrlFormat = StringUtils.trim(prop.getProperty(DEVICE_URL_KEY));
				} finally {
					in.close();
				}
			} else {
				logger.info("[initProperties] Load {} file failed.", CMDB_API_URL_FILE);
				throw new RuntimeException();
			}
		} catch (Exception e) {
			logger.info("[initProperties] Load {} file failed.", CMDB_API_URL_FILE);
			throw new RuntimeException(e);
		}
	}

	private String transformEnv() {
		if (env.equals("dev")) {
			return "dev";
		} else if (env.equals("alpha")) {
			return "alpha";
		} else if (env.equals("beta") || env.equals("qa")) {
			return "beta";
		} else if (env.equals("prelease") || env.equals("ppe")) {
			return "ppe";
		} else if (env.equals("perf")) {
			return "perf";
		} else if (env.equals("product")) {
			return "生产";
		} else {
			throw new IllegalArgumentException("unsupported env type " + env);
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initProperties();
	}
}

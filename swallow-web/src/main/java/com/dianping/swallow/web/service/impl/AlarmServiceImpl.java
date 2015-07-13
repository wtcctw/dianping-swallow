package com.dianping.swallow.web.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.AlarmDao;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.SendType;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.util.NetUtil;

/**
 * 
 * @author qiyin
 * 
 */

@Service("alarmService")
public class AlarmServiceImpl implements AlarmService {

	private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);

	private static final String AlARM_URL_FILE = "alarm-url.properties";

	private static final String MAIL_KEY = "mail";
	private static final String WEIXIN_KEY = "weiXin";
	private static final String SMS_KEY = "sms";

	private static final String RECIEVER_SPLIT = ",";

	private String mailUrl;
	private String smsUrl;
	private String weiXinUrl;

	@Autowired
	private AlarmDao alarmDao;

	@Autowired
	private HttpService httpService;

	@Autowired
	private IPDescManager ipDescManager;

	public AlarmServiceImpl() {
		try {
			InputStream in = AlarmServiceImpl.class.getClassLoader().getResourceAsStream(AlARM_URL_FILE);
			if (in != null) {
				if (logger.isInfoEnabled()) {
					logger.info("loading " + AlARM_URL_FILE);
				}

				Properties prop = new Properties();
				try {
					prop.load(in);
					setMailUrl(StringUtils.trim(prop.getProperty(MAIL_KEY)));
					setWeiXinUrl(StringUtils.trim(prop.getProperty(WEIXIN_KEY)));
					setSmsUrl(StringUtils.trim(prop.getProperty(SMS_KEY)));
				} catch (IOException e) {
					if (logger.isInfoEnabled()) {
						logger.info("Load alarm config file failed.");
					}
				} finally {
					in.close();
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean sendSms(String mobile, String title, String body, AlarmType type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("body", "[" + title + "]" + body));
		boolean result = httpService.httpPost(getSmsUrl(), params).isSuccess();
		insert(new Alarm().buildType(type).buildReceiver(mobile).buildTitle(title).buildBody(body)
				.buildSendType(SendType.SMS).buildSourceIp(NetUtil.IP).buildCreateTime(new Date()));
		return result;
	}

	@Override
	public boolean sendWeixin(String email, String title, String content, AlarmType type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("content", content));
		boolean result = httpService.httpPost(getWeiXinUrl(), params).isSuccess();
		insert(new Alarm().buildType(type).buildReceiver(email).buildTitle(title).buildBody(content)
				.buildSendType(SendType.WEIXIN).buildSourceIp(NetUtil.IP).buildCreateTime(new Date()));
		return result;
	}

	@Override
	public boolean sendMail(String email, String title, String content, AlarmType type) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("recipients", email));
		params.add(new BasicNameValuePair("body", content));
		boolean result = httpService.httpPost(getMailUrl(), params).isSuccess();
		insert(new Alarm().buildType(type).buildReceiver(email).buildTitle(title).buildBody(content)
				.buildSendType(SendType.SMS).buildSourceIp(NetUtil.IP).buildCreateTime(new Date()));
		return result;
	}

	@Override
	public void sendAll(String ip, String title, String message, AlarmType type) {
		IPDesc ipDesc = ipDescManager.getIPDesc(ip);
		if (ipDesc != null) {
			if (StringUtils.isNotBlank(ipDesc.getDpMobile())) {
				String[] mobiles = StringUtils.split(ipDesc.getDpMobile(), RECIEVER_SPLIT);
				for (String mobile : mobiles) {
					if (StringUtils.isBlank(mobile)) {
						continue;
					}
					sendSms(mobile, title, message, type);
				}
			}
			if (StringUtils.isNotBlank(ipDesc.getEmail())) {
				String[] emails = StringUtils.split(ipDesc.getEmail(), RECIEVER_SPLIT);
				for (String email : emails) {
					if (StringUtils.isBlank(email)) {
						continue;
					}
					sendWeixin(ipDesc.getEmail(), title, message, type);
					sendMail(ipDesc.getEmail(), title, message, type);
				}
			}

		} else {
			logger.error("[doCheckPort] cannot find ipDesc info.");
		}
	}

	public String getMailUrl() {
		return mailUrl;
	}

	public void setMailUrl(String mailUrl) {
		this.mailUrl = mailUrl;
	}

	public String getSmsUrl() {
		return smsUrl;
	}

	public void setSmsUrl(String smsUrl) {
		this.smsUrl = smsUrl;
	}

	public String getWeiXinUrl() {
		return weiXinUrl;
	}

	public void setWeiXinUrl(String weiXinUrl) {
		this.weiXinUrl = weiXinUrl;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	public void setAlarmDao(AlarmDao alarmDao) {
		this.alarmDao = alarmDao;
	}

	@Override
	public boolean insert(Alarm alarm) {
		return alarmDao.insert(alarm);
	}

	@Override
	public boolean update(Alarm alarm) {
		return alarmDao.update(alarm);
	}

	@Override
	public int deleteById(String id) {
		return alarmDao.deleteById(id);
	}

	@Override
	public Alarm findById(String id) {
		return alarmDao.findById(id);
	}

	@Override
	public Map<String, Object> findByReceiver(String receiver, int offset, int limit) {
		return alarmDao.findByReceiver(receiver, offset, limit);
	}

	@Override
	public Map<String, Object> findByCreateTime(Date createTime, int offset, int limit) {
		return alarmDao.findByCreateTime(createTime, offset, limit);
	}

	@Override
	public List<Alarm> findAll() {
		return alarmDao.findAll();
	}

}

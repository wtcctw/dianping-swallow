package com.dianping.swallow.web.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.swallow.web.dao.AlarmDao;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.alarm.SendType;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.util.NetUtil;

/**
 * 
 * @author qiyin
 * 
 */

@Service("alarmService")
public class AlarmServiceImpl implements AlarmService, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);

	private static final String AlARM_URL_FILE = "alarm-url.properties";

	private static final String MAIL_KEY = "mail";
	private static final String WEIXIN_KEY = "weiXin";
	private static final String SMS_KEY = "sms";

	private static final String env = "[" + EnvZooKeeperConfig.getEnv().trim() + "]";

	private static final String LEFT_BRACKET = "[";

	private static final String RIGHT_BRACKET = "]";

	private static final String NEW_LINE_SIGN = "\n";

	private String mailUrl;
	private String smsUrl;
	private String weiXinUrl;

	@Autowired
	private AlarmDao alarmDao;

	@Autowired
	private HttpService httpService;

	public AlarmServiceImpl() {

	}

	@Override
	public boolean sendSms(Alarm alarm) {
		if (StringUtils.isBlank(alarm.getReceiver())) {
			return true;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mobile", alarm.getReceiver()));
		String title = LEFT_BRACKET + alarm.getNumber() + RIGHT_BRACKET + alarm.getTitle() + env;
		params.add(new BasicNameValuePair("body", title + NEW_LINE_SIGN + alarm.getBody()));
		boolean result = httpService.httpPost(getSmsUrl(), params).isSuccess();
		insert(alarm.setSendType(SendType.SMS).setSourceIp(NetUtil.IP).setCreateTime(new Date()));
		return result;
	}

	@Override
	public boolean sendWeiXin(Alarm alarm) {
		if (StringUtils.isBlank(alarm.getReceiver())) {
			return true;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", alarm.getReceiver()));
		String title = LEFT_BRACKET + alarm.getNumber() + RIGHT_BRACKET + alarm.getTitle() + env;
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("content", alarm.getBody()));
		boolean result = httpService.httpPost(getWeiXinUrl(), params).isSuccess();
		insert(alarm.setSendType(SendType.WEIXIN).setSourceIp(NetUtil.IP).setCreateTime(new Date()));
		return result;
	}

	@Override
	public boolean sendMail(Alarm alarm) {
		if (StringUtils.isBlank(alarm.getReceiver())) {
			return true;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String title = LEFT_BRACKET + alarm.getNumber() + RIGHT_BRACKET + alarm.getTitle() + env;
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("recipients", alarm.getReceiver()));
		params.add(new BasicNameValuePair("body", alarm.getBody()));
		boolean result = httpService.httpPost(getMailUrl(), params).isSuccess();
		insert(alarm.setSendType(SendType.SMS).setSourceIp(NetUtil.IP).setCreateTime(new Date()));
		return result;
	}

	public boolean sendSms(Set<String> mobiles, Alarm alarm) {
		if (mobiles != null) {
			Iterator<String> iterator = mobiles.iterator();
			while (iterator.hasNext()) {
				String mobile = iterator.next();
				alarm.setReceiver(mobile);
				sendSms(alarm);
			}
		}
		return true;
	}

	public boolean sendMail(Set<String> emails, Alarm alarm) {
		if (emails != null) {
			Iterator<String> iterator = emails.iterator();
			while (iterator.hasNext()) {
				String email = iterator.next();
				alarm.setReceiver(email);
				sendMail(alarm);
			}
		}
		return true;
	}

	public boolean sendWeiXin(Set<String> emails, Alarm alarm) {
		if (emails != null) {
			Iterator<String> iterator = emails.iterator();
			while (iterator.hasNext()) {
				String email = iterator.next();
				alarm.setReceiver(email);
				sendWeiXin(alarm);
			}
		}
		return true;
	}

	public String getMailUrl() {
		return mailUrl;
	}

	public String getSmsUrl() {
		return smsUrl;
	}

	public String getWeiXinUrl() {
		return weiXinUrl;
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
	public List<Alarm> findByReceiver(String receiver, int offset, int limit) {
		return alarmDao.findByReceiver(receiver, offset, limit);
	}

	@Override
	public List<Alarm> findByCreateTime(Date createTime, int offset, int limit) {
		return alarmDao.findByCreateTime(createTime, offset, limit);
	}

	@Override
	public long countByCreateTime(Date createTime) {
		return alarmDao.countByCreateTime(createTime);
	}

	@Override
	public long countByReceiver(String receiver) {
		return alarmDao.countByReceiver(receiver);
	}

	private void initProperties() {
		try {
			InputStream in = AlarmServiceImpl.class.getClassLoader().getResourceAsStream(AlARM_URL_FILE);
			if (in != null) {
				Properties prop = new Properties();
				try {
					prop.load(in);
					mailUrl = StringUtils.trim(prop.getProperty(MAIL_KEY));
					weiXinUrl = StringUtils.trim(prop.getProperty(WEIXIN_KEY));
					smsUrl = StringUtils.trim(prop.getProperty(SMS_KEY));
				} finally {
					in.close();
				}
			} else {
				logger.info("[initProperties] Load {} file failed.", AlARM_URL_FILE);
				throw new RuntimeException();
			}
		} catch (Exception e) {
			logger.info("[initProperties] Load {} file failed.", AlARM_URL_FILE);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initProperties();
	}

	@Override
	public List<Alarm> findByReceiverAndTime(String receiver, Date startTime, Date endTime, int offset, int limit) {
		return alarmDao.findByReceiverAndTime(receiver, startTime, endTime, offset, limit);
	}

	@Override
	public long countByReceiverAndTime(String receiver, Date startTime, Date endTime) {
		return alarmDao.countByReceiverAndTime(receiver, startTime, endTime);
	}

}

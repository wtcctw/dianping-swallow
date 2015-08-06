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
import com.dianping.swallow.web.model.alarm.ResultType;
import com.dianping.swallow.web.model.alarm.SendInfo;
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

	private static final String NOPERSON_RECEIVER = "NOPERSON";

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
	public boolean sendSms(String mobile, String title, String body) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("body", title + NEW_LINE_SIGN + body));
		boolean result = httpService.httpPost(getSmsUrl(), params).isSuccess();
		if (!result) {
			result = httpService.httpPost(getSmsUrl(), params).isSuccess();
		}
		return result;
	}

	@Override
	public boolean sendWeiXin(String email, String title, String content) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("content", content));
		boolean result = httpService.httpPost(getWeiXinUrl(), params).isSuccess();
		if (!result) {
			result = httpService.httpPost(getWeiXinUrl(), params).isSuccess();
		}
		return result;
	}

	@Override
	public boolean sendMail(String email, String title, String content) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("title", title));
		params.add(new BasicNameValuePair("recipients", email));
		params.add(new BasicNameValuePair("body", content));
		boolean result = httpService.httpPost(getMailUrl(), params).isSuccess();
		if (!result) {
			result = httpService.httpPost(getMailUrl(), params).isSuccess();
		}
		return result;
	}

	@Override
	public boolean sendSms(Alarm alarm, String receiver) {
		alarm.setId(null).setSourceIp(NetUtil.IP).setCreateTime(new Date());
		if (StringUtils.isBlank(receiver)) {
			alarm.addSendInfo(new SendInfo().setReceiver(NOPERSON_RECEIVER).setResultType(ResultType.FAILED)
					.setSendType(SendType.SMS));
			return false;
		}
		String title = LEFT_BRACKET + alarm.getNumber() + RIGHT_BRACKET + alarm.getTitle() + env;
		boolean result = sendSms(receiver, title, alarm.getBody());
		ResultType resultType = result ? ResultType.SUCCESS : ResultType.FAILED;
		alarm.addSendInfo(new SendInfo().setReceiver(receiver).setResultType(resultType).setSendType(SendType.SMS));
		return result;
	}

	@Override
	public boolean sendWeiXin(Alarm alarm, String receiver) {
		alarm.setId(null).setSourceIp(NetUtil.IP).setCreateTime(new Date());
		if (StringUtils.isBlank(receiver)) {
			alarm.addSendInfo(new SendInfo().setReceiver(NOPERSON_RECEIVER).setResultType(ResultType.FAILED)
					.setSendType(SendType.WEIXIN));
			return false;
		}
		String title = LEFT_BRACKET + alarm.getNumber() + RIGHT_BRACKET + alarm.getTitle() + env;
		boolean result = sendWeiXin(receiver, title, alarm.getBody());
		ResultType resultType = result ? ResultType.SUCCESS : ResultType.FAILED;
		alarm.addSendInfo(new SendInfo().setReceiver(receiver).setResultType(resultType).setSendType(SendType.WEIXIN));
		return result;
	}

	@Override
	public boolean sendMail(Alarm alarm, String receiver) {
		alarm.setId(null).setSourceIp(NetUtil.IP).setCreateTime(new Date());
		if (StringUtils.isBlank(receiver)) {
			alarm.addSendInfo(new SendInfo().setReceiver(NOPERSON_RECEIVER).setResultType(ResultType.FAILED)
					.setSendType(SendType.MAIL));
			return false;
		}
		String title = LEFT_BRACKET + alarm.getNumber() + RIGHT_BRACKET + alarm.getTitle() + env;
		boolean result = sendMail(receiver, title, alarm.getBody());
		ResultType resultType = result ? ResultType.SUCCESS : ResultType.FAILED;
		alarm.addSendInfo(new SendInfo().setReceiver(receiver).setResultType(resultType).setSendType(SendType.WEIXIN));
		return result;
	}

	public boolean sendSms(Set<String> mobiles, Alarm alarm) {
		if (mobiles != null && mobiles.size() > 0) {
			Iterator<String> iterator = mobiles.iterator();
			while (iterator.hasNext()) {
				String mobile = iterator.next();
				sendSms(alarm, mobile);
			}
		} else {
			sendSms(alarm, StringUtils.EMPTY);
		}
		return true;
	}

	public boolean sendMail(Set<String> emails, Alarm alarm) {
		if (emails != null && emails.size() > 0) {
			Iterator<String> iterator = emails.iterator();
			while (iterator.hasNext()) {
				String email = iterator.next();
				sendMail(alarm, email);
			}
		} else {
			sendMail(alarm, StringUtils.EMPTY);
		}
		return true;
	}

	public boolean sendWeiXin(Set<String> emails, Alarm alarm) {
		if (emails != null && emails.size() > 0) {
			Iterator<String> iterator = emails.iterator();
			while (iterator.hasNext()) {
				String email = iterator.next();
				sendWeiXin(alarm, email);
			}
		} else {
			sendWeiXin(alarm, StringUtils.EMPTY);
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

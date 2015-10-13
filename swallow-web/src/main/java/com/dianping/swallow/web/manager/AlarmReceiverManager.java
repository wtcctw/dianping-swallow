package com.dianping.swallow.web.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:52:44
 */
public interface AlarmReceiverManager {

	AlarmReceiver getSwallowReceiver();

	AlarmReceiver getAlarmReceiverByName(String appName);

	AlarmReceiver getAlarmReceiverByName(List<String> appNames);

	AlarmReceiver getAlarmReceiverByIp(String ip);

	AlarmReceiver getAlarmReceiverByIp(List<String> ips);

	AlarmReceiver getAlarmReceiverByPTopic(String topicName);

	AlarmReceiver getAlarmReceiverByConsumerId(String topicName, String consumerId);

	public static class AlarmReceiver {

		public AlarmReceiver() {
			this.emails = new HashSet<String>();
			this.mobiles = new HashSet<String>();
		}

		public AlarmReceiver(Set<String> emails, Set<String> mobiles) {
			this.emails = new HashSet<String>();
			this.emails.addAll(emails);
			this.mobiles = new HashSet<String>();
			this.mobiles.addAll(mobiles);
		}

		private Set<String> emails;

		private Set<String> mobiles;

		public void addAlarmReceiver(AlarmReceiver alarmReceiver) {
			if (alarmReceiver != null) {
				if (alarmReceiver.getEmails() != null) {
					this.emails.addAll(alarmReceiver.getEmails());
				}
				if (alarmReceiver.getMobiles() != null) {
					this.mobiles.addAll(alarmReceiver.getMobiles());
				}

			}
		}

		public void addEmail(String email) {
			this.emails.add(email);
		}

		public void addEmails(Collection<String> emails) {
			this.emails.addAll(emails);
		}

		public void addMobile(String mobile) {
			this.mobiles.add(mobile);
		}

		public void addMobiles(Collection<String> mobiles) {
			this.mobiles.addAll(mobiles);
		}

		public Set<String> getEmails() {
			return emails;
		}

		public void setEmails(Set<String> emails) {
			this.emails = emails;
		}

		public Set<String> getMobiles() {
			return mobiles;
		}

		public void setMobiles(Set<String> mobiles) {
			this.mobiles = mobiles;
		}
	}

}

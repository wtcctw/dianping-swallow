package com.dianping.swallow.web.alarmer;

/**
 *
 * @author qiyin
 *
 */
public interface ConsumerServiceAlarmer extends Alarmer {
	
	public void doCheckProcess();

	public void doCheckPort();

	public void doCheckService();

	public void doCheckSender();
}

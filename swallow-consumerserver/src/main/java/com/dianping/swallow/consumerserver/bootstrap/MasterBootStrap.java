package com.dianping.swallow.consumerserver.bootstrap;

import com.dianping.swallow.consumerserver.config.ConfigManager;

/**
 * master启动
 * @author mengwenchao
 *
 * 2015年6月15日 下午5:04:31
 */
public class MasterBootStrap extends AbstractBootStrap {

	private static boolean isSlave = false;

	private MasterBootStrap() {
	}

	public static void main(String[] args) throws Exception {

		new MasterBootStrap().run();
	}

	private void run() throws Exception {

		createContext();

		startConsumerServer();

		if (logger.isInfoEnabled()) {
			logger.info("[run][start working]");
		}

		createShutdownHook();

		int masterPort = ConfigManager.getInstance().getMasterPort();
		startNetty(masterPort);

	}

	@Override
	protected boolean isSlave() {
		return isSlave;
	}
}

package com.dianping.swallow.web.model.server;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午3:42:14
 */
public class ConsumerMasterServer extends ConsumerServer {

	public ConsumerMasterServer(String ip, int port) {
		super(ip, port);
	}

	@Override
	public void checkService() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "ConsumerMasterServer [] " + super.toString();
	}

	@Override
	public void initServer() {
		super.initServer();
	}

}

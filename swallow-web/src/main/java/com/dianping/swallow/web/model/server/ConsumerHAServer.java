package com.dianping.swallow.web.model.server;

import com.dianping.swallow.web.model.event.ServerType;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午3:42:23
 */
public class ConsumerHAServer implements Sendable, Serviceable {

	private boolean isPortLastAlarmed = false;

	private volatile boolean isSlaveUsing = false;

	private ConsumerServer slaveServer;

	private ConsumerServer masterServer;

	public ConsumerHAServer(ConsumerServer masterServer, ConsumerServer slaveServer) {
		this.setSlaveServer(slaveServer);
		this.setMasterServer(masterServer);
		isPortLastAlarmed = false;
		isSlaveUsing = false;
	}

	public ConsumerServer getSlaveServer() {
		return slaveServer;
	}

	public void setSlaveServer(ConsumerServer slaveServer) {
		this.slaveServer = slaveServer;
	}

	public ConsumerServer getMasterServer() {
		return masterServer;
	}

	public void setMasterServer(ConsumerServer masterServer) {
		this.masterServer = masterServer;
	}

	public void checkPort() {
		boolean isMasterOpened = masterServer.isPortOpen();
		boolean isSlaveOpened = slaveServer.isPortOpen();
		String masterIp = masterServer.getIp();
		String slaveIp = slaveServer.getIp();
		if (!isMasterOpened && isSlaveOpened) {
			isSlaveUsing = true;
			masterServer.report(masterIp, slaveIp, ServerType.SLAVEPORT_OPENED);
			isPortLastAlarmed = true;
		} else if (isMasterOpened && isSlaveOpened) {
			isSlaveUsing = false;
			masterServer.report(masterIp, slaveIp, ServerType.BOTHPORT_OPENED);
			isPortLastAlarmed = true;
		} else if (!isMasterOpened && !isSlaveOpened) {
			isSlaveUsing = false;
			masterServer.report(masterIp, slaveIp, ServerType.BOTHPORT_UNOPENED);
			isPortLastAlarmed = true;
		} else {
			isSlaveUsing = false;
			if (isPortLastAlarmed) {
				masterServer.report(masterIp, slaveIp, ServerType.PORT_OPENED_OK);
			}
			isPortLastAlarmed = false;
		}
	}

	@Override
	public void checkSender(long sendTimeStamp) {
		if (!isSlaveUsing) {
			masterServer.checkSender(sendTimeStamp);
		} else {
			slaveServer.checkSender(sendTimeStamp);
		}
	}

	@Override
	public void checkService() {
		slaveServer.checkService();
	}

	@Override
	public String toString() {
		return "ConsumerHAServer [isPortLastAlarmed=" + isPortLastAlarmed + ", isSlaveUsing=" + isSlaveUsing
				+ ", slaveServer=" + slaveServer + ", masterServer=" + masterServer + "]";
	}

}

package com.dianping.swallow.web.model.server;

public class ConsumerHAServer {

	private ConsumerServer slaveServer;

	private ConsumerServer masterServer;

	public ConsumerHAServer(ConsumerServer slaveServer, ConsumerServer masterServer) {
		this.setSlaveServer(slaveServer);
		this.setMasterServer(masterServer);
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

}

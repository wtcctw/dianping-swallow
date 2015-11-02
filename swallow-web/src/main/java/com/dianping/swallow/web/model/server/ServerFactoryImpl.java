package com.dianping.swallow.web.model.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.service.HttpService;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午3:41:37
 */
@Service("serverFactory")
public class ServerFactoryImpl implements ServerFactory {

	@Autowired
	private ServerConfig serverConfig;

	@Autowired
	private HttpService httpService;

	@Autowired
	private EventFactory eventFactory;

	@Autowired
	private EventReporter eventReporter;

	@Override
	public ProducerServer createProducerServer(String ip) {
		ProducerServer producerServer = new ProducerServer(ip);
		setComponent(producerServer);
		producerServer.initServer();
		return producerServer;
	}

	@Override
	public ConsumerServer createConsumerServer(String ip, int port, boolean isMaster) {
		ConsumerServer consumerServer = null;
		if (isMaster) {
			consumerServer = new ConsumerMasterServer(ip, port);
		} else {
			consumerServer = new ConsumerSlaveServer(ip, port);
		}
		setComponent(consumerServer);
		consumerServer.initServer();
		return consumerServer;
	}

	@Override
	public ConsumerHAServer createConsumerHAServer(ConsumerServer masterServer, ConsumerServer slaveServer) {
		ConsumerHAServer consumerHAServer = new ConsumerHAServer(masterServer, slaveServer);
		return consumerHAServer;
	}

	private void setComponent(Server server) {
		server.setServerConfig(serverConfig);
		server.setHttpService(httpService);
		server.setEventReporter(eventReporter);
		server.setEventFactory(eventFactory);
	}
}

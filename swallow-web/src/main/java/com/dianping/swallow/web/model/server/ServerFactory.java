package com.dianping.swallow.web.model.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.service.HttpService;

@Service("serverFactory")
public class ServerFactory {

	@Autowired
	private HttpService httpService;
	
	@Autowired
	private EventFactory eventFactory;
	
	@Autowired
	private EventReporter eventReporter;
	
	public ProducerServer createProducerServer(String ip) {
		ProducerServer producerServer = new ProducerServer(ip);
		setComponent(producerServer);
		return producerServer;
	}
	
	public ConsumerServer createConsumerServer(String ip){
		ConsumerServer consumerServer = new ConsumerServer(ip);
		setComponent(consumerServer);
		return consumerServer;
	}

	private void setComponent(Server server) {
		server.setHttpService(httpService);
		server.setEventReporter(eventReporter);
		server.setEventFactory(eventFactory);
	}
}

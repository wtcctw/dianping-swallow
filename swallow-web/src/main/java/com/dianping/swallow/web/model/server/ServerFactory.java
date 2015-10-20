package com.dianping.swallow.web.model.server;

/**
 * 
 * @author qiyin
 *
 *         2015年10月19日 上午9:32:28
 */
public interface ServerFactory {

	ProducerServer createProducerServer(String ip);

	ConsumerServer createConsumerServer(String ip, int port, boolean isMaster);

	ConsumerHAServer createConsumerHAServer(ConsumerServer masterServer, ConsumerServer slaveServer);

}

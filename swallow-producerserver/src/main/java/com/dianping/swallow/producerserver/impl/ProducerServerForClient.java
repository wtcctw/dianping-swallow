package com.dianping.swallow.producerserver.impl;

import java.util.HashMap;
import java.util.Map;

import com.dianping.cat.message.internal.MessageId;
import com.dianping.dpsf.api.ServiceRegistry;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.packet.Packet;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.packet.PktProducerGreet;
import com.dianping.swallow.common.internal.packet.PktSwallowPACK;
import com.dianping.swallow.common.internal.producer.ProducerSwallowService;
import com.dianping.swallow.common.internal.util.DateUtils;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.ServerDaoException;
import com.dianping.swallow.producerserver.MessageReceiver.VALID_STATUS;

public class ProducerServerForClient extends AbstractProducerServer implements ProducerSwallowService {

	private static final int DEFAULT_PORT = 4000;

	private int port = DEFAULT_PORT;
	private String remoteServiceName;

	private boolean autoSelectPort;

	public ProducerServerForClient() {
	}

	/**
	 * 启动producerServerClient
	 * 
	 * @param port
	 *            供producer连接的端口
	 * @throws RemoteServiceInitFailedException
	 *             远程调用初始化失败
	 * @throws Exception
	 *             连续绑定同一个端口抛出异常，pigeon初始化失败抛出异常
	 */
	@Override
	protected void doStart() throws Exception {

		super.doStart();
		try {
			ServiceRegistry remoteService = null;
			remoteService = new ServiceRegistry(getPort());
			remoteService.setAutoSelectPort(autoSelectPort);
			Map<String, Object> services = new HashMap<String, Object>();
			services.put(remoteServiceName, this);
			remoteService.setServices(services);
			remoteService.init();
			if (logger.isInfoEnabled()) {
				logger.info("[Initialize pigeon sucessfully, Producer service for client is ready.]");
			}
			System.out.println(DateUtils.current()
					+ "[Initialize pigeon sucessfully, Producer service for client is ready.]");// 用来检查系统启动成功
		} catch (Exception e) {
			logger.error("[Initialize pigeon failed.]", e);
			throw new RemoteServiceInitFailedException(e);
		}
	}

	/**
	 * 保存swallowMessage到数据库
	 * 
	 * @throws ServerDaoException
	 */
	@Override
	public Packet sendMessage(Packet pkt) throws ServerDaoException {
		if (pkt == null) {
			throw new IllegalArgumentException("Argument of remote service could not be null.");
		}
		
		SwallowMessage swallowMessage;
		String topicName;
		
		Packet pktRet = null;
		switch (pkt.getPacketType()) {
		
			case PRODUCER_GREET:
				if (logger.isInfoEnabled()) {
					logger.info("[Got Greet][From=" + ((PktProducerGreet) pkt).getProducerIP() + "][Version="
							+ ((PktProducerGreet) pkt).getProducerVersion() + "]");
				}
				// 返回ProducerServer地址
				pktRet = new PktSwallowPACK(producerServerIP);
				break;
			case OBJECT_MSG:
				
				topicName = ((PktMessage) pkt).getDestination().getName();
	
				VALID_STATUS validStatus = messageReceiver.isTopicNameValid(topicName);
				if (!(validStatus == VALID_STATUS.SUCCESS)) {
					throw new IllegalArgumentException("Invalid topic(" + topicName+ "), " + validStatus);
				}
	
	
				swallowMessage = ((PktMessage) pkt).getContent();
				String sourceDomain = null;
				try {
					sourceDomain = MessageId.parse(((PktMessage) pkt).getCatEventID()).getDomain();
				} catch (Exception e) {
				}
	
				try {
					
					messageReceiver.receiveMessage(topicName, sourceDomain, swallowMessage);
					pktRet = new PktSwallowPACK(swallowMessage.getSha1());		
				} catch (Exception e) {
					logger.error("[Save message to DB failed.]", e);
					throw new ServerDaoException(e);
				}
				break;
			default:
				logger.warn("[Received unrecognized packet.]" + pkt);
				break;
		}
		return pktRet;
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getRemoteServiceName() {
		return remoteServiceName;
	}

	public void setRemoteServiceName(String remoteServiceName) {
		this.remoteServiceName = remoteServiceName;
	}

	public boolean isAutoSelectPort() {
		return autoSelectPort;
	}

	public void setAutoSelectPort(boolean autoSelectPort) {
		this.autoSelectPort = autoSelectPort;
	}
}

package com.dianping.swallow.consumer.internal;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.codec.JsonDecoder;
import com.dianping.swallow.common.internal.codec.JsonEncoder;
import com.dianping.swallow.common.internal.heartbeat.HeartBeatSender;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.processor.ConsumerProcessor;
import com.dianping.swallow.common.internal.processor.DefaultMessageProcessorTemplate;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.threadfactory.PullStrategy;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener;
import com.dianping.swallow.consumer.internal.action.RetryOnAllExceptionActionWrapper;
import com.dianping.swallow.consumer.internal.action.RetryOnBackoutMessageExceptionActionWrapper;
import com.dianping.swallow.consumer.internal.config.ConfigManager;
import com.dianping.swallow.consumer.internal.netty.ConsumerConnectionListener;
import com.dianping.swallow.consumer.internal.netty.MessageClientHandler;
import com.dianping.swallow.consumer.internal.task.LongTaskChecker;
import com.dianping.swallow.consumer.internal.task.TaskChecker;

public class ConsumerImpl implements Consumer, ConsumerConnectionListener {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerImpl.class);

	private String consumerId;

	private Destination dest;

	private MessageListener listener;

	private InetSocketAddress masterAddress;

	private InetSocketAddress slaveAddress;

	private volatile AtomicBoolean started = new AtomicBoolean(false);

	private ConsumerConfig config;

	private final String consumerIP = IPUtil.getFirstNoLoopbackIP4Address();

	private ClientBootstrap bootstrap;

	private ExecutorService service;

	private ConsumerThread masterConsumerThread;

	private ConsumerThread slaveConsumerThread;

	private ConsumerProcessor processor;

	private PullStrategy pullStrategy;

	private ExecutorService consumerHelperExecutors;

	private TaskChecker taskChecker;
	
	private HeartBeatSender heartBeatSender;

	public ConsumerImpl(Destination dest, ConsumerConfig config, InetSocketAddress masterAddress,
			InetSocketAddress slaveAddress, HeartBeatSender heartBeatManager) {
		this(dest, null, config, masterAddress, slaveAddress, heartBeatManager);
	}

	public ConsumerImpl(Destination dest, String consumerId, ConsumerConfig config, InetSocketAddress masterAddress,
			InetSocketAddress slaveAddress, HeartBeatSender heartBeatSender) {

		checkArgument(config, consumerId);
		
		// ack#<topic>#<cid>长度不超过63字节(mongodb对数据库名的长度限制是63字节)
		int length = 0;
		length += dest.getName().length();
		length += consumerId != null ? consumerId.length() : 0;
		if (length > 58) {
			throw new IllegalArgumentException(
					"TopicName and consumerId's summary length must less or equals 58 ：topicName is " + dest.getName()
							+ ", consumerId is " + consumerId);
		}

		this.dest = dest;
		this.consumerId = consumerId;
		this.config = config == null ? new ConsumerConfig() : config;
		this.masterAddress = masterAddress;
		this.slaveAddress = slaveAddress;
		this.processor = new DefaultMessageProcessorTemplate();
		this.taskChecker = new LongTaskChecker(config.getLongTaskAlertTime());
		this.pullStrategy = new DefaultPullStrategy(config.getDelayBaseOnBackoutMessageException(),
				config.getDelayUpperboundOnBackoutMessageException());
		this.heartBeatSender = heartBeatSender;

	}

	@Override
	public void start() {
		if (listener == null) {
			throw new IllegalArgumentException(
					"MessageListener is null, MessageListener should be set(use setListener()) before start.");
		}
		if (started.compareAndSet(false, true)) {

			if (logger.isInfoEnabled()) {
				logger.info("Starting " + this.toString());
			}
			service = Executors.newFixedThreadPool(this.getConfig().getThreadPoolSize(), new MQThreadFactory("swallow-consumer-client-" + consumerId + "-"));
			startListener();
			startHelper();
		}
	}

	private void startListener() {
		final MessageClientHandler handler = new MessageClientHandler(this, processor, taskChecker,
				createRetryWrapper(), this);
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		bootstrap.setOption("keepAlive", true);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("jsonDecoder", new JsonDecoder(PktMessage.class));
				pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
				pipeline.addLast("jsonEncoder", new JsonEncoder(PktConsumerMessage.class));
				pipeline.addLast("handler", handler);
				return pipeline;
			}
		});
		// 启动连接master的线程
		masterConsumerThread = new ConsumerThread();
		masterConsumerThread.setBootstrap(bootstrap);
		masterConsumerThread.setRemoteAddress(masterAddress);
		masterConsumerThread.setInterval(ConfigManager.getInstance().getConnectMasterInterval());
		masterConsumerThread.setName("masterConsumerThread");
		masterConsumerThread.start();
		// 启动连接slave的线程
		slaveConsumerThread = new ConsumerThread();
		slaveConsumerThread.setBootstrap(bootstrap);
		slaveConsumerThread.setRemoteAddress(slaveAddress);
		slaveConsumerThread.setInterval(ConfigManager.getInstance().getConnectSlaveInterval());
		slaveConsumerThread.setName("slaveConsumerThread");
		slaveConsumerThread.start();
	}

	@Override
	public void onChannelConnected(Channel channel) {
		heartBeatSender.addChannel(channel);
	}

	@Override
	public void onChannelDisconnected(Channel channel) {
		heartBeatSender.removeChannel(channel);
	}
	
	private void startHelper() {
		
		consumerHelperExecutors = Executors.newCachedThreadPool(new MQThreadFactory("Swallow-Helper-"));
		consumerHelperExecutors.execute(taskChecker);
	}	

	private SwallowCatActionWrapper createRetryWrapper() {

		if (listener instanceof MessageRetryOnAllExceptionListener) {
			return new RetryOnAllExceptionActionWrapper(pullStrategy, config.getRetryCount());
		}
		return new RetryOnBackoutMessageExceptionActionWrapper(pullStrategy, config.getRetryCount());
	}

	@Override
	public void close() {
		if (started.compareAndSet(true, false)) {

			if (logger.isInfoEnabled()) {
				logger.info("Closing " + this.toString());
			}
			service.shutdown();
			closeListerner();
			closeHelpers();

		}
	}

	private void closeHelpers() {
		consumerHelperExecutors.shutdown();
		taskChecker.close();
	}

	private void closeListerner() {
		masterConsumerThread.interrupt();
		slaveConsumerThread.interrupt();
		bootstrap.releaseExternalResources();
	}

	public void submit(Runnable task) {
		if (!isClosed() && this.service != null && !this.service.isShutdown()) {
			try {
				this.service.execute(task);
			} catch (RuntimeException e) {
				logger.warn("Error when submiting task, task is ignored（message will retry by server later）: "
						+ e.getMessage());
			}
		}
	}

	private void checkArgument(ConsumerConfig config, String consumerId) {
		
		if (ConsumerType.NON_DURABLE == config.getConsumerType()) {// 非持久类型，不能有consumerId
			if (consumerId != null) {
				throw new IllegalArgumentException("ConsumerId should be null when consumer type is NON_DURABLE");
			}
		} else {// 持久类型，需要验证consumerId
			if (!NameCheckUtil.isConsumerIdValid(consumerId)) {
				throw new IllegalArgumentException(
						"ConsumerId is invalid, should be [0-9,a-z,A-Z,'_','-'], begin with a letter, and length is 2-30 long："
								+ consumerId);
			}
		}

	}

	@Override
	public String toString() {
		return String.format("ConsumerImpl [consumerId=%s, dest=%s, masterAddress=%s, slaveAddress=%s, config=%s]",
				consumerId, dest, masterAddress, slaveAddress, config);
	}

	public String getConsumerIP() {
		return consumerIP;
	}

	public boolean isClosed() {
		return !started.get();
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public Destination getDest() {
		return dest;
	}

	public void setDest(Destination dest) {
		this.dest = dest;
	}

	public MessageListener getListener() {
		return listener;
	}

	@Override
	public void setListener(MessageListener listener) {
		this.listener = listener;
	}

	public ConsumerConfig getConfig() {
		return config;
	}
	
	public TaskChecker getTaskChecker(){
		return taskChecker;
	}
}

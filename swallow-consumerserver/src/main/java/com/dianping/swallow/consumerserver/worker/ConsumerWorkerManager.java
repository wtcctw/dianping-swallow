package com.dianping.swallow.consumerserver.worker;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.dao.AckDAO;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.lifecycle.AbstractLifecycle;
import com.dianping.swallow.common.internal.lifecycle.DefaultLifecycleManager;
import com.dianping.swallow.common.internal.lifecycle.LifecycleCallback;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.ProxyUtil;
import com.dianping.swallow.common.internal.util.task.AbstractEternalTask;
import com.dianping.swallow.consumerserver.Heartbeater;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.pool.ConsumerThreadPoolManager;

public class ConsumerWorkerManager extends AbstractLifecycle{

    private static final Logger               logger                   = LoggerFactory
            .getLogger(ConsumerWorkerManager.class);

    private final long                        ACKID_UPDATE_INTERVAL = ConfigManager.getInstance()
            .getAckIdUpdateIntervalSecond() * 1000;
    
    private final long                        MESSAGE_SEND_NONE_INTERVAL = ConfigManager.getInstance().getMessageSendNoneInterval();

    private AckDAO                            ackDAO;
    private Heartbeater                       heartbeater;
    private SwallowBuffer                     swallowBuffer;
    private MessageDAO                        messageDAO;

    private ConsumerAuthController            consumerAuthController;

    private MQThreadFactory                   threadFactory         = new MQThreadFactory();

    private Map<ConsumerInfo, ConsumerWorker> consumerInfo2ConsumerWorker;

    private List<Thread>					  threads = new LinkedList<Thread>();

    private volatile boolean                  readyForAcceptConn    = true;

    private ConsumerThreadPoolManager  		 consumerThreadPoolManager;

    private DefaultLifecycleManager 		lifecycleManager;
    
    public ConsumerWorkerManager(){
    	
    	lifecycleManager = new DefaultLifecycleManager();
    }
    
    public void setAckDAO(AckDAO ackDAO) {
        this.ackDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(ackDAO, ConfigManager.getInstance()
                .getRetryIntervalWhenMongoException());
    }

    public MQThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setHeartbeater(Heartbeater heartbeater) {
        this.heartbeater = heartbeater;
    }

    public void setSwallowBuffer(SwallowBuffer swallowBuffer) {
        this.swallowBuffer = swallowBuffer;
    }

    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(messageDAO, ConfigManager.getInstance()
                .getRetryIntervalWhenMongoException());
    }

    public void handleGreet(Channel channel, ConsumerInfo consumerInfo, int clientThreadCount,
                            MessageFilter messageFilter, long startMessageId) {
        if (!readyForAcceptConn) {
            //接收到连接，直接就关闭它
            channel.close();
        } else {
            findOrCreateConsumerWorker(consumerInfo, messageFilter, startMessageId).handleGreet(channel, clientThreadCount);
        }
    }

    public void handleAck(Channel channel, ConsumerInfo consumerInfo, Long ackedMsgId, ACKHandlerType type) {
        ConsumerWorker worker = findConsumerWorker(consumerInfo);
        if (worker != null) {
            if (ackedMsgId != null) {
                worker.handleAck(channel, ackedMsgId, type);
            }
        } else {
            logger.warn(consumerInfo + "ConsumerWorker is not exist!");
            channel.close();
        }
    }

    public void handleChannelDisconnect(Channel channel, ConsumerInfo consumerInfo) {
        ConsumerWorker worker = findConsumerWorker(consumerInfo);
        if (worker != null) {
            worker.handleChannelDisconnect(channel);
        }
    }

    @Override
    public void dispose() throws Exception{

    	lifecycleManager.dispose(new LifecycleCallback() {
			
			@Override
			public void onTransition() {
		        try {
		            long waitAckTimeWhenCloseSwc = ConfigManager.getInstance().getWaitAckTimeWhenCloseSwc();
		            logger.info("Sleeping " + waitAckTimeWhenCloseSwc + "ms to wait receiving client's Acks.");
		            Thread.sleep(waitAckTimeWhenCloseSwc);
		            logger.info("Sleep done.");
		        } catch (InterruptedException e) {
		            logger.error("Close Swc thread InterruptedException", e);
		        }

		        //关闭ConsumerWorker的资源（关闭内部的“用于获取消息的队列”）
		        if (consumerInfo2ConsumerWorker != null) {
		            for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
		                entry.getValue().close();
		            }
		        }

		        //清空 “用于保存状态的” 2个map
		        if (consumerInfo2ConsumerWorker != null) {
		            consumerInfo2ConsumerWorker.clear();
		        }
			}
		});
    }

    private ConsumerWorker findConsumerWorker(ConsumerInfo consumerInfo) {
        return consumerInfo2ConsumerWorker.get(consumerInfo);
    }

    public Map<ConsumerInfo, ConsumerWorker> getConsumerId2ConsumerWorker() {
        return consumerInfo2ConsumerWorker;
    }

    private ConsumerWorker findOrCreateConsumerWorker(ConsumerInfo consumerInfo, MessageFilter messageFilter, long startMessageId) {
        ConsumerWorker worker = findConsumerWorker(consumerInfo);
        
        if(logger.isInfoEnabled()){
        	logger.info("[findOrCreateConsumerWorker][startMessageId]" + consumerInfo + "," + startMessageId);
        }
        
        if(startMessageId != -1){
        	cleanConsumerInfo(consumerInfo, worker);
        }
        if (worker == null) {
            // 以ConsumerId(String)为同步对象，如果是同一个ConsumerId，则串行化
            synchronized (consumerInfo.getConsumerId().intern()) {
                if ((worker = findConsumerWorker(consumerInfo)) == null) {
                    worker = new ConsumerWorkerImpl(consumerInfo, this, messageFilter, consumerAuthController, consumerThreadPoolManager, startMessageId);
                    consumerInfo2ConsumerWorker.put(consumerInfo, worker);
                }
            }
        }
        return worker;
    }

	private void cleanConsumerInfo(ConsumerInfo consumerInfo,
			ConsumerWorker worker) {
    	messageDAO.cleanMessage(consumerInfo.getDest().getName(), consumerInfo.getConsumerId());
        if (worker != null) {
            worker.close();
            worker = null;
            consumerInfo2ConsumerWorker.remove(consumerInfo);
        }
	}

	public void isSlave(boolean isSlave) {
        if (!isSlave) {
            startHeartbeater(ConfigManager.getInstance().getMasterIp());
        }
    }

	@Override
    public void initialize() throws Exception {
		
		lifecycleManager.initialize(new LifecycleCallback() {
			
			@Override
			public void onTransition() {
		        readyForAcceptConn = true;
		        consumerInfo2ConsumerWorker = new ConcurrentHashMap<ConsumerInfo, ConsumerWorker>();
			}
		});
    }

	@Override
	public void start() throws Exception{
		lifecycleManager.start(new LifecycleCallback() {
			
			@Override
			public void onTransition() {
		        startSendMessageThread();
		        startIdleWorkerCheckerThread();
		        startAckIdUpdaterThread();
			}
		});
	}
	
	@Override
	public void stop() throws Exception{
		lifecycleManager.stop(new LifecycleCallback() {
			
			@Override
			public void onTransition() {
			}
		});
		
	}
	
	private void startTask(Runnable task, String threadName){
		
		Thread thread = threadFactory.newThread(task, threadName);
		thread.start();
		threads.add(thread);
	}
	
	private void startSendMessageThread() {
		
		int coreSize = Runtime.getRuntime().availableProcessors();
		ExecutorService executors = Executors.newFixedThreadPool(coreSize, new MQThreadFactory("MessageSenderTricker"));
		
		for(int i=0; i < coreSize; i++){
			executors.execute(new AbstractEternalTask() {
				
				private volatile boolean shouldSleep = false;
				
				@Override
				public void sleep(){
					if(shouldSleep){
						try {
							if(logger.isDebugEnabled()){
								logger.debug("[startSendMessageThread][sleep]" + MESSAGE_SEND_NONE_INTERVAL);
							}
							TimeUnit.MICROSECONDS.sleep(MESSAGE_SEND_NONE_INTERVAL);
						} catch (InterruptedException e) {
						}
					}
				}
				@Override
				protected boolean stop() {
					return shoudStop();
				}
				
				@Override
				protected void doRun() {
					shouldSleep = true;
	                for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
	                    ConsumerWorker worker = entry.getValue();
	                    boolean messageExist = worker.sendMessage();
	                    if(messageExist && shouldSleep){
	                    	shouldSleep = false;
	                    }
	                }
	                
				}
			}
			);
		}
		
	}

	protected boolean shoudStop() {
		
		if(lifecycleManager.isDisposed() || lifecycleManager.isStopped()){
			return true;
		}
		return false;
	}

	
	private void startAckIdUpdaterThread() {
        startTask(new AbstractEternalTask() {
			
        	@Override
        	public void sleep(){
                try {
                    Thread.sleep(ACKID_UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
        	}
        	
			@Override
			protected boolean stop() {
				return shouldStop();
			}
			
			@Override
			protected void doRun() {
                for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
                    ConsumerWorker worker = entry.getValue();
                    worker.recordAck();
                }
			}
        }, "AckIdUpdaterThread-");
	}

	private boolean shouldStop() {

		return !(lifecycleManager.isInitialized() || lifecycleManager.isStarted());
	}

	
    private void startIdleWorkerCheckerThread() {
    	
    	startTask(new AbstractEternalTask() {
    		
    		@Override
    		public void sleep(){
                try {
                    Thread.sleep(ConfigManager.getInstance().getCheckConnectedChannelInterval());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
    		}
			
			@Override
			protected boolean stop() {
				return shoudStop();
			}
			
			@Override
			protected void doRun() {
                //轮询所有ConsumerWorker，如果其已经没有channel，则关闭ConsumerWorker,并移除
                for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
                    ConsumerWorker worker = entry.getValue();
                    ConsumerInfo consumerInfo = entry.getKey();
                    if (worker.allChannelDisconnected()) {
                        worker.recordAck();
                        removeConsumerWorker(consumerInfo);
                        worker.close();
                        logger.info("[doRun][close ConsumerWorker]ConsumerWorker for " + consumerInfo + " has no connected channel, close it");
                    }
                }
			}
        }, "idleConsumerWorkerChecker-");
    }

    private void startHeartbeater(final String ip) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                while (true) {

                    try {
                        heartbeater.beat(ip);
                        Thread.sleep(ConfigManager.getInstance().getHeartbeatUpdateInterval());
                    } catch (Exception e) {
                        logger.error("Error update heart beat", e);
                    }
                }
            }

        };

        Thread heartbeatThread = threadFactory.newThread(runnable, "heartbeat-");
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }

    /**
     * consumerId对应的ConsumerWorker已经没有任何连接，所以移除ConsumerWorker
     */
    private void removeConsumerWorker(ConsumerInfo consumerInfo) {
        consumerInfo2ConsumerWorker.remove(consumerInfo);
    }

    public AckDAO getAckDAO() {
        return ackDAO;
    }

    public SwallowBuffer getSwallowBuffer() {
        return swallowBuffer;
    }

    public MessageDAO getMessageDAO() {
        return messageDAO;
    }

    public void setConsumerAuthController(ConsumerAuthController consumerAuthController) {
        this.consumerAuthController = consumerAuthController;
    }


	public ConsumerThreadPoolManager getConsumerThreadPoolManager() {
		return consumerThreadPoolManager;
	}
	
	public void setConsumerThreadPoolManager(ConsumerThreadPoolManager consumerThreadPoolManager) {
		this.consumerThreadPoolManager = consumerThreadPoolManager;
	}

}

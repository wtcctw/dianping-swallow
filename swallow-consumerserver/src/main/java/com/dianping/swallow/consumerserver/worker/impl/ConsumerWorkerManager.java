package com.dianping.swallow.consumerserver.worker.impl;

import io.netty.channel.Channel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;




import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.dao.AckDAO;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.lifecycle.LifecycleCallback;
import com.dianping.swallow.common.internal.lifecycle.MasterSlaveComponent;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.lifecycle.impl.DefaultLifecycleManager;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.internal.util.ProxyUtil;
import com.dianping.swallow.common.internal.util.task.AbstractEternalTask;
import com.dianping.swallow.common.server.monitor.collector.ConsumerCollector;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.pool.ConsumerThreadPoolManager;
import com.dianping.swallow.consumerserver.worker.ConsumerWorker;

/**
 * @author mengwenchao
 *
 * 2015年8月17日 下午5:09:21
 */
public class ConsumerWorkerManager extends AbstractLifecycle implements MasterSlaveComponent{

    private final long                        ACKID_UPDATE_INTERVAL = ConfigManager.getInstance().getAckIdUpdateIntervalMili();
    
    private final long                        MESSAGE_SEND_NONE_INTERVAL = ConfigManager.getInstance().getMessageSendNoneInterval();

    private AckDAO                            ackDAO;
    private SwallowBuffer                     swallowBuffer;
    private MessageDAO                        messageDAO;

    private ConsumerAuthController            consumerAuthController;

    private MQThreadFactory                   threadFactory         = new MQThreadFactory();

    private Map<ConsumerInfo, ConsumerWorker> consumerInfo2ConsumerWorker;

    private List<Thread>					  threads = new LinkedList<Thread>();

    private volatile boolean                  readyForAcceptConn    = true;

    private ConsumerThreadPoolManager  		 consumerThreadPoolManager;

    private DefaultLifecycleManager 		lifecycleManager;
    
    private ConsumerCollector 				consumerCollector;

    private int senderThreadSize 			= CommonUtils.getCpuCount();
    
	private ExecutorService sendMessageExecutor  	=  null;

    
    public ConsumerWorkerManager(){
    	
    	lifecycleManager = new DefaultLifecycleManager(this);
    }
    
    public void setAckDAO(AckDAO ackDAO) {
        this.ackDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(ackDAO, ConfigManager.getInstance()
                .getRetryIntervalWhenMongoException(), 
                ConfigManager.getInstance().getRetryTimesWhenMongoException());
    }

    public MQThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setSwallowBuffer(SwallowBuffer swallowBuffer) {
        this.swallowBuffer = swallowBuffer;
    }

    public void setMessageDAO(MessageDAO messageDAO) {
        this.messageDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(messageDAO, 
        		ConfigManager.getInstance().getRetryIntervalWhenMongoException(), ConfigManager.getInstance().getRetryTimesWhenMongoException());
    }

    public void handleGreet(Channel channel, ConsumerInfo consumerInfo, int clientThreadCount,
                            MessageFilter messageFilter, long startMessageId) {
        if (!readyForAcceptConn) {
            //接收到连接，直接就关闭它
            channel.close();
        } else {
        	synchronized (consumerInfo2ConsumerWorker) {
                findOrCreateConsumerWorker(consumerInfo, startMessageId).handleGreet(channel, clientThreadCount, messageFilter);
			}
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
    
	public void handleHeartBeat(Channel channel, ConsumerInfo consumerInfo) {
        ConsumerWorker worker = findConsumerWorker(consumerInfo);
        if (worker != null) {
        	worker.handleHeartBeat(channel);
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
				
		        if (consumerInfo2ConsumerWorker != null) {
		            for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
		                try {
							entry.getValue().dispose();
						} catch (Exception e) {
							logger.error("[onTransition]", e);
						}
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

    private ConsumerWorker findOrCreateConsumerWorker(ConsumerInfo consumerInfo, long startMessageId) {
        ConsumerWorker worker = findConsumerWorker(consumerInfo);
        
        if(logger.isInfoEnabled()){
        	logger.info("[findOrCreateConsumerWorker][startMessageId]" + consumerInfo + "," + startMessageId);
        }
        
        if(startMessageId != -1){
        	cleanConsumerInfo(consumerInfo, worker);
        	worker = null;
        }
        
        if (worker == null) {
            // 以ConsumerId(String)为同步对象，如果是同一个ConsumerId，则串行化
        	if(logger.isInfoEnabled()){
        		logger.info("[findOrCreateConsumerWorker][create ConsumerWorkerImpl]" + consumerInfo);
        	}
        	
            synchronized (consumerInfo.getConsumerId().intern()) {
            	
                if ((worker = findConsumerWorker(consumerInfo)) == null) {
                    worker = new ConsumerWorkerImpl(consumerInfo, this, consumerAuthController, consumerThreadPoolManager, startMessageId, consumerCollector);
                    try {
						worker.initialize();
					} catch (Exception e) {
						logger.error("[findOrCreateConsumerWorker][init error]", e);
					}
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
            try {
				worker.dispose();
			} catch (Exception e) {
				logger.error("[cleanConsumerInfo]", e);
			}
            worker = null;
            consumerInfo2ConsumerWorker.remove(consumerInfo);
        }
	}

	@Override
    public void initialize() throws Exception {
		
		lifecycleManager.initialize(new LifecycleCallback() {
			
			@Override
			public void onTransition() {
		        readyForAcceptConn = true;
		        consumerInfo2ConsumerWorker = new ConcurrentHashMap<ConsumerInfo, ConsumerWorker>();
		        
				senderThreadSize = ConfigManager.getInstance().getMessageSendThreadPoolSize();
				if(senderThreadSize <= 0){
					senderThreadSize = CommonUtils.getCpuCount();
				}
				
			}
		});
    }

	@Override
	public void start() throws Exception{
		lifecycleManager.start(new LifecycleCallback() {
			
			@Override
			public void onTransition() {
				
				sendMessageExecutor = Executors.newFixedThreadPool(senderThreadSize, new MQThreadFactory("MessageSenderTricker"));
				
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
				recordAck();
				
				for(Thread thread : threads){
					thread.interrupt();
				}
				threads.clear();
				sendMessageExecutor.shutdownNow();
			}
		});
		
	}
	
	private void startTask(Runnable task, String threadName){
		
		Thread thread = threadFactory.newThread(task, threadName);
		thread.start();
		threads.add(thread);
	}
	
	private void startSendMessageThread() {
		
		for(int i=0; i < senderThreadSize; i++){
			sendMessageExecutor.execute(new SendMessageThread(i, senderThreadSize));
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
			protected void doOnThreadExit() {
				threads.remove(Thread.currentThread());
			}

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
				recordAck();
			}
        }, "AckIdUpdaterThread-");
	}

	
	protected void recordAck(){
		
        for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
            ConsumerWorker worker = entry.getValue();
            worker.recordAck();
        }
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
			protected void doOnThreadExit() {
				threads.remove(Thread.currentThread());
			}
			
			@Override
			protected void doRun() {
                //轮询所有ConsumerWorker，如果其已经没有channel，则关闭ConsumerWorker,并移除
	        	synchronized (consumerInfo2ConsumerWorker) {

	                for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
	                    ConsumerWorker worker = entry.getValue();
	                    ConsumerInfo consumerInfo = entry.getKey();
	                    if (worker.allChannelDisconnected()) {
	                    	if(logger.isInfoEnabled()){
	                    		logger.info("[doRun][clean]" + consumerInfo);
	                    	}
	                        worker.recordAck();
	                        removeConsumerWorker(consumerInfo);
	                        try {
								worker.dispose();
							} catch (Exception e) {
								logger.error("[doRun]", e);
							}
	                        logger.info("[doRun][close ConsumerWorker]ConsumerWorker for " + consumerInfo + " has no connected channel, close it");
	                    }
	                }
	                
	                if(logger.isInfoEnabled()){
	                	logger.info("[doRun][consumerWorker count]" + consumerInfo2ConsumerWorker.size());
	                }
	        	}
			}
        }, "idleConsumerWorkerChecker-");
    }

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

	public ConsumerCollector getConsumerCollector() {
		return consumerCollector;
	}

	public void setConsumerCollector(ConsumerCollector consumerCollector) {
		this.consumerCollector = consumerCollector;
	}

	class SendMessageThread extends AbstractEternalTask{
		
		private volatile boolean shouldSleep = false;
		private final int index, total;
		
		public SendMessageThread(int index, int total){
			this.index = index;
			this.total = total;
		}
		
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
			Iterator<ConsumerWorker>   ite = consumerInfo2ConsumerWorker.values().iterator();
			
			int i = 0;
			while(ite.hasNext()){
				
				ConsumerWorker worker = ite.next();
				i++;
				if((i % total) == index){
					boolean messageExist = worker.sendMessage();
	                if(messageExist && shouldSleep){
	                	shouldSleep = false;
	                }
				}
            }
		}
	}
}

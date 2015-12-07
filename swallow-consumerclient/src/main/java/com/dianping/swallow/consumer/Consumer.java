package com.dianping.swallow.consumer;



import com.dianping.swallow.common.internal.observer.Observer;
import com.dianping.swallow.common.message.Destination;

public interface Consumer extends Observer{

	/**
	 * 获取消息目的
	 * 
	 * @return
	 */
	Destination getDest();

	/**
	 * 获取consumerId
	 * 
	 * @return
	 */
	String getConsumerId();

	/**
	 * 启动消费。
	 */
	void start();

	/**
	 * 设置listener，用于回调
	 * 
	 * @param listener
	 */
	void setListener(MessageListener listener);

	
	/**
	 * 获取listener
	 * @return
	 */
	MessageListener getListener();

	/**
	 * 关闭Consumer。关闭后，将与server断开连接，不再接收消息。<br>
	 * 
	 * 注意：调用close后，仍然可以通过调用start，重新启动消费。
	 */
	void close();
	
	/**
	 * 是否关闭
	 * @return
	 */
	boolean isClosed();
}

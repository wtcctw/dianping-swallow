package com.dianping.swallow.consumerserver.buffer;

import java.util.List;
import java.util.Queue;

import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.observer.Observer;

/**
 * @author mengwenchao
 *
 * 2015年8月17日 下午3:46:58
 */
public interface CloseableBlockingQueue<E> extends Queue<E> {

   /**
    * 关闭BlockingQueue占用的资源
    */
   void close();

   /**
    * 是否已经关闭BlockingQueue占用的资源
    */
   void isClosed();

   
	/**
	 * 在队列为空的前提下，返回最大Id
	 * @param isBackup
	 * @return
	 */
	Long getEmptyTailMessageId();


	void putMessage(List<SwallowMessage> messages);

	/**
	 * @param tailId
	 */
	void setTailMessageId(Long tailId);

	/**
	 * @return
	 */
	Long getTailMessageId();

	/**
	 * @param messageRetriever
	 */
	void setMessageRetriever(MessageRetriever messageRetriever);
	
}

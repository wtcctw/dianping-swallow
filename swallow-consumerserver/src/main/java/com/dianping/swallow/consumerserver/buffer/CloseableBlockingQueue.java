package com.dianping.swallow.consumerserver.buffer;

import java.util.Queue;

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
	Long getEmptyTailMessageId(boolean isBackup);

}

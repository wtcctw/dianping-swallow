package com.dianping.swallow.consumer;

public interface Consumer {
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
    * 关闭Consumer。关闭后，将与server断开连接，不再接收消息。<br>
    * 
    * 注意：调用close后，仍然可以通过调用start，重新启动消费。
    */
   void close();
}

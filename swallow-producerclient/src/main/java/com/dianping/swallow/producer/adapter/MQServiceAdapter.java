package com.dianping.swallow.producer.adapter;

import java.util.Map;

import com.dianping.swallow.Destination;
import com.dianping.swallow.MQService;
import com.dianping.swallow.MessageConsumer;
import com.dianping.swallow.MessageProducer;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerFactory;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

public class MQServiceAdapter implements MQService{
   
   ProducerFactory producerFactory;
   
   public MQServiceAdapter(String mongoUri) throws RemoteServiceInitFailedException{
      producerFactory = ProducerFactoryImpl.getInstance();
   }

   @Override
   public MessageProducer createProducer(Destination dest, Map<ProducerOptionKey, Object> options) {
      if(dest == null){
         throw new IllegalArgumentException("Illegal Argument!");
      }
      ProducerConfig config = new ProducerConfig();
      config.setMode(ProducerMode.ASYNC_MODE);//adapter模式升级，默认是异步模式。

      if (options != null) {
          try {
              String mode = (String) options.get("mode");
              if (mode != null && mode.equalsIgnoreCase("SYNC_MODE")) {
                  config.setMode(ProducerMode.SYNC_MODE);
              }
              String filequeueBaseDir = (String) options.get("filequeueBaseDir");
              if (filequeueBaseDir != null) {
                  config.setFilequeueBaseDir(filequeueBaseDir);
              }

              //retryTimes
              int retryTimes = Integer.parseInt(options.get(ProducerOptionKey.MsgSendRetryCount).toString());

              if (retryTimes == -1) {
                  config.setAsyncRetryTimes(Integer.MAX_VALUE);
                  config.setSyncRetryTimes(Integer.MAX_VALUE);
              } else {
                  config.setAsyncRetryTimes(retryTimes);
                  config.setSyncRetryTimes(retryTimes);
              }
          } catch (Exception nfe) {
          }
      }
      
      return new MessageProducerAdapter(producerFactory.createProducer(com.dianping.swallow.common.message.Destination.topic(dest.getName()), config));
   }

   @Override
   public MessageProducer createProducer(Destination dest) {
      return createProducer(dest, null);
   }

   @Override
   public MessageConsumer createConsumer(Destination dest, Map<ConsumerOptionKey, Object> options) {
      throw new RuntimeException("Can not support old SwallowConsumer creation any more!");
   }

   @Override
   public MessageConsumer createConsumer(Destination dest) {
      throw new RuntimeException("Can not support old SwallowConsumer creation any more!");
   }

   @Override
   public void close() {
      
   }
}

[TOC]
# 0.7.1
### heartbeat线程命名
### 客户端日志格式调整
### 非dev环境默认使用lion配置

<<<<<<< HEAD
# 0.7.0
### 客户端增加心跳
### 增加消息接收接口
	``com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener``
	用户实现此接口的话，如果``onMessage``抛出异常，即进行重试。
=======
#0.7.0
## client
### consumer客户端增加心跳
### consumer增加接收消息接口
	增加接口:``com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener``，户实现此接口的话，如果``onMessage``抛出异常，即进行重试。
>>>>>>> test
	在旧的接口``com.dianping.swallow.consumer.MessageListener``中，只有在抛出``BackoutMessageException``异常时才会重试。
### 增加超时监测
	当用户``onMessage``内的业务逻辑运行时间过长的话，在cat中显示超时Transaction信息
### 升级依赖的中间件版本

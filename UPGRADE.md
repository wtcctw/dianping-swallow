[TOC]
# 0.9 (to be released)
##  支持topic partition
##  支持consumer server横向扩展
##  支持producer server横向扩展

# 0.8.2(to be released)
## 消息EXACTLY_ONCE
## 消息时序


# 0.8.1(to be released)
## client
### client log不跟随业务日志
## server
### 支持存储迁移
### 支持消息缓存


# 0.8.0
## client:
### netty4升级
### 支持topic对应的consumer server迁移
## server:
### 支持kafka，性能提升

# 0.7.1
### heartbeat线程命名
### 客户端日志格式调整
### 非dev环境默认使用lion配置

#0.7.0
## client
### consumer客户端增加心跳
### consumer增加接收消息接口
	增加接口:``com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener``，户实现此接口的话，如果``onMessage``抛出异常，即进行重试。
	在旧的接口``com.dianping.swallow.consumer.MessageListener``中，只有在抛出``BackoutMessageException``异常时才会重试。
### 增加超时监测
	当用户``onMessage``内的业务逻辑运行时间过长的话，在cat中显示超时Transaction信息
### 升级依赖的中间件版本


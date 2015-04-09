[TOC]

#0.7.0
## client
### 客户端增加心跳
### 增加接口``com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener``
	用户实现此接口的话，如果``onMessage``抛出异常，即进行重试。
	在旧的接口``com.dianping.swallow.consumer.MessageListener``中，只有在抛出``BackoutMessageException``异常时才会重试。
### 增加超时监测，当用户``onMessage``内的业务逻辑运行时间过长的话，在cat中显示超时Transaction信息
### 升级依赖的中间件版本

## server
### 梳理打包脚本、支持在code平台上进行各环境的发布
### 合并、优化search团队自定义swallow代码
### swallow依赖中间件版本升级
### 服务器线程优化

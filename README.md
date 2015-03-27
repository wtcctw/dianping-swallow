Swallow使用说明
==============


[TOC]

# swallow基础概念

	Swallow 是什么:
* Swallow是一个基于Topic的异步消息传送系统。Swallow使用发布/订阅消息的传送模型，消息发布者指定Topic并发送消息到Swallow消息服务器，消息订阅者则指定Topic并从Swallow消息服务器订阅消息。
* Swallow的发布/订阅模型。消息由Producer发布，ProducerServer负责接收并存储消息到DB。ConsumerServer负责从DB获取消息，并推送给Consumer。
* Swallow支持集群订阅者。在集群中，使用相同ConsumerId(例如Consumer A)的Consumer，将会视作同一个Consumer（同一个Consumer消费的Message将不会重复）。例如，假设一个有2台机器(主机1和主机2)的集群，ConsumerId都是“Consumer-A”，那么同一则Message，将要么被“主机1”获取，要么被“主机2”获取，不会被两者均获取。

# swallow可用系统
##w
# swallow系统接入流程
## 申请topic
# swallow使用说明
## 使用swallow发送消息
## 使用swallow接收消息
# swallow常见问题以及处理
##

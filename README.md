# Swallow使用说明

* * * 

### 1. Swallow基础概念

* Swallow 是什么:
	* Swallow是一个`基于Topic的异步消息传送系统`。Swallow使用`发布/订阅消息`的传送模型，`消息发布者`指定Topic并发送消息到Swallow消息服务器,`消息订阅者`则指定Topic并从Swallow消息服务器订阅消息。
	* Swallow的发布/订阅模型。消息由Producer发布，ProducerServer负责接收并存储消息到DB。ConsumerServer负责从DB获取消息，并推送给Consumer。
	* Swallow`支持集群订阅者`。在集群中，使用相同ConsumerId(例如Consumer A)的Consumer，将会视作同一个Consumer（同一个Consumer消费的Message将不会重复）。例如，假设一个有2台机器(主机1和主机2)的集群，ConsumerId都是“Consumer-A”，那么`同一则Message，将要么被“主机1”获取，要么被“主机2”获取，不会被两者均获取`。

### 2. Swallow可用系统
### 3. Swallow系统接入流程

* 申请topic

### 4. Swallow使用说明

* 使用Swallow发送消息
* 使用Swallow接收消息

* * * 

### 5. Swallow常见问题以及处理

* #### a. 如何查看我的消费是否有延迟、延迟多少条消息？
	* 从[CAT](http://cat.dp/)中查看`Swallow`项目的`Transaction`，可以获得相应的信息（[传送门](http://cat.dp/cat/r/t?op=view&domain=Swallow)）。
	* 以dp\_action这个topic为例（`仅作示例，具体到自己的topic，请做相应变通`），先找到`In:dp_action`这个type：
	![Swallow Transaction In CAT](http://code.dianpingoa.com/arch/swallow/raw/master/readme/1.png)
	* 上图右边对应的是当前该topic的producer生产的`消息总量`，点击`In:dp_action`链接，可以看到每个producer产生的消息数量：
	![Producer Count In CAT](http://code.dianpingoa.com/arch/swallow/raw/master/readme/2.png)
	* 返回上一级，在同一级页面中，找到`Out:dp_action`这个type，对应从consumer server发出的消息数量：
	![Producer Count In CAT](http://code.dianpingoa.com/arch/swallow/raw/master/readme/3.png)
	* `Out:dp_action`对应的数量为消费这个topic的`所有consumer`消费的`消息总量`，点击进入，可以看到`每个消费者单台消费机`的消费数量：
	![Producer Count In CAT](http://code.dianpingoa.com/arch/swallow/raw/master/readme/4.png)
	* 对于一个consumer id来说，消费的消息总量，应该等于producer生产的消息总量（In:dp\_action的数量），`如果消费的消息总量小于生产的消息总量，那么消费是有延迟的`。

* #### b. 如何查看我的Consumer消费一条消息的平均时间？
	* 从[CAT](http://cat.dp/)中查看`Consumer ID对应项目`的Transaction，找到`MsgConsumed`和`MsgConsumeTried`这两个type：
	![Producer Count In CAT](http://code.dianpingoa.com/arch/swallow/raw/master/readme/5.png)
	* `MsgConsumed`表示`consumer server给这个consumer推送的消息数量`，`MsgConsumeTried`表示`consumer尝试消费消息的次数`，如果存在失败重试，则MsgConsumeTried数量可能会比MsgConsumed更多。
	* 右边的三列可以看出`consumer调用onMessage回调函数耗费的最小、最大以及平均时间`，如果consumer消费状况一直良好，突然某个时刻开始有消费延时，可以观察一下这里的平均时间是不是比之前更高，如果平均消费时间比正常情况高出很多，可能会造成消费延时。

* #### c. 我的Consumer有延时，该怎么确认问题所在？
	* 首先观察consumer的`平均消费时间`是否存在异常，如果consumer的平均消费时间`比正常情况高出许多`，说明onMessage回调函数依赖的服务存在问题，可以考虑_最近的代码变更_，或询问_依赖的服务_是否存在故障。
	* 如果consumer的`平均消费时间一直很高`，说明consumer的消费线程数太少，可以考虑`修改配置文件增加消费线程数`，或者`扩容应用增加消费机`。
	* 在cat中观察consumer的problem，`如果swallow相关异常过多，请及时联系swallow团队成员`。
	* 如果consumer的平均消费时间`一直正常、没有发生突变`，则有可能是swallow的consumer server负载较高或存在其他故障，`此时请及时联系swallow团队成员`。

* #### d. 我的Consumer堵了，该怎么确认问题所在？
	* 首先`确认consumer是否已经正确启动`：
		* 增加一些`健康监测页面`或其他机制以判断consumer是否正确启动。
		* 查看自己`应用日志`以及/data/applogs/tomcat/`catalina.out`日志，确认没有影响应用正常启动的异常出现。
	* `确认topic是否有生产者在持续生产消息`，可以参考[问题1](#q1)，连续查看swallow中的transaction，看是否存在数量变化，如果In:<topic名称>没有变化，说明没有新的消息产生，而不是consumer堵住了。
	* 确认consumer是否在持续消费消息，可以参考[问题2](#q2)，连续查看consumer对应项目的transaction，看MsgConsumed这个type是否数量增加，如果这个数量在增加，说明consumer消费没有堵住。
	* 其次确认`是否该topic其他consumer都在消费，只有自己的consumer停止消费了`。可以参考[问题1](#q1)，查看topic其他consumer的消费情况。
		* __如果该topic其他consumer也都停止消费，且生产者正常工作，`请及时联系swallow团队成员`__。
		* 如果该topic其他consumer消费正常，只有你自己的consumer消费堵住了，请查看consumer对应项目在`CAT`中的`Problem`，找到`Heartbeat`这个type，查询最新的`线程堆栈`，以确认Consumer的线程是否block在onMessage方法内：
		
		![Producer Count In CAT](http://code.dianpingoa.com/arch/swallow/raw/master/readme/6.png)
		
		* 如果consumer的线程block在onMessage方法内，说明onMessage方法内调用存在异常情况，可能原因`包括但不限于``死循环`、`等待IO`、`死锁`、`数据库操作`、`依赖的服务超时`等情况，请仔细检查这些情况，`修复并重启consumer`即可。
		* 如果consumer的线程不存在block现象，`请及时联系swallow团队成员`。
* #### e. 如何确认我的Producer正常工作？
	* 首先确认生产者是否正常启动，判别方法跟[问题4](#q4)中第一点类似，增加检测页面，确保日志中没有影响正常启动的异常出现。
	* 在`CAT`上观察`Producer对应项目`的transaction，找到`MsgProduced`以及`MsgProduceTried`这两个Type，`MsgProduced`的数量表示`程序产生的消息数量`，`MsgProduceTried`表示Swallow的`producer client尝试发送给producer server的次数`。
	![Producer Count In CAT](http://code.dianpingoa.com/arch/swallow/raw/master/readme/7.png)
	* 正常情况下这两个type的数量是一一对应的，如果设置了重试，在发送失败的情况下，producer会重新尝试发送指定次数，此时MsgProduceTried的数量会大于MsgProduced的数量。如果一段时间内没有新消息发送成功，则可以认为没有新消息产生，或者Producer存在问题，`此时请联系swallow团队成员`。
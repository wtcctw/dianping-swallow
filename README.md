注：**本文针对Swallow0.6.10版本**

# 基础概念

## 功能介绍

 Swallow是一个*基于Topic的异步消息传送系统*，使用*发布/订阅*的消息模型，*消息发布者*指定Topic并发送消息到Swallow服务器，*消息订阅者*从Swallow服务器订阅消息。

 主要功能：

* 系统解耦，解除消息生产者和消费者之间的强依赖关系
* 峰值处理，短时间大量消息涌入，可以将消息写入消息队列，消费系统可以后续慢慢消费
* 系统交互消息管理，查询系统之间的历史通讯记录、进行问题追溯以及故障查询工作


## 名词解释

* Topic：表示消息的主题
* 发布/订阅模型：同一个topic可以同时被多个订阅者订阅，订阅者在订阅时指定订阅ID(ConsumerId)。使用相同ConsumerId(例如Consumer A)的Consumer，将会视作同一个Consumer（同一个Consumer消费的Message将不会重复）。假设一个集群，有2台机器(主机1和主机2)，ConsumerId都是“Consumer-A”，那么*同一则Message，将要么被“主机1”获取，要么被“主机2”获取，不会被两者均获取*。
* Producer：表示生产消息的主体，将消息发送到目的Topic。
* Consumer：表示消费消息的主体，从Topic中获取消息。
* 同步模式：发送方发出消息后，等待接收服务器成功、超时、异常时返回。
* 异步模式：生产者发送消息时，先把消费存储到本地文件，同时启动后台发送消息线程将文件的消息读取出来发送到server。

# Swallow线下消息收发消息模拟平台

## 生产者模拟平台

* 生产者可以向某个Topic发送一条消息。

	* [alpha环境](http://192.168.8.21:7070/rundemo/swallow-alpha-067#r=0&j=17)
	* [qa环境](http://192.168.8.21:7070/rundemo/swallow-qa-067#r=0&j=17)

* 在代码编辑框内，修改“Destination.topic("example")”为“Destination.topic("<你的topic名称>")”)。
* 点击右边绿色run按钮。
* 在右边紫色控制台下方，输入消息的内容，按回车，即可发送。

## 消费者模拟平台

* 消费者接收消息  (在代码编辑框内，修改“Destination.topic("example")”为“Destination.topic("<你的topic名称>")”)，点击“run”，即可启动消费者。

	* [alpha环境](http://192.168.8.21:7070/rundemo/swallow-alpha#r=0&j=10 )
	* [qa环境](http://192.168.8.21:7070/rundemo/swallow-qa#r=0&j=10)

# Swallow系统接入流程

## 申请Topic

如果有新的Topic，请联系：孟文超/宋通(wenchao.meng@dianping.com, tong.song@dianping.com，待帮您配置后，方可使用（线下和线上均可以使用），未申请的topic使用时会遇到拒绝连接的异常。

联系时，**请邮件里告知：**

* 申请人所在业务部门	(例如：支付中心业务部门)
* 使用Swallow解决的业务场景是什么	(例如：订单支付成功后，使用Swallow通知xxx付款成功的消息)
* topic名称(例如：dp_action)，不能包含点(.)，只使用字母和下划线，长度不超过25个字符
* 生产者业务名，以及负责人姓名	(例如，pay-order, 林俊杰)
* 消费者业务名，以及负责人姓名  (例如，mobile-api, 陆经天)
* 计划上线时间
* 每天大概的消息量	(例如，5万条 ， 请注意不要写错，比如每日100万消息，应该写“100万”，不要写错成"100")

# Swallow使用说明


## 使用Swallow发送消息
### 添加maven依赖
	<dependency>
	    <groupId>org.springframework</groupId>
	    <artifactId>spring-beans</artifactId>
	    <version>4.1.3.RELEASE</version>
	</dependency>
	<dependency>
	    <groupId>org.springframework</groupId>
	    <artifactId>spring-context</artifactId>
	    <version>4.1.3.RELEASE</version>
	</dependency>
	<dependency>
	    <groupId>org.springframework</groupId>
	    <artifactId>spring-core</artifactId>
	    <version>4.1.3.RELEASE</version>
	</dependency>
	<dependency>
	    <groupId>com.dianping.swallow</groupId>
	    <artifactId>swallow-producerclient</artifactId>
	    <version>0.6.10</version> 
	</dependency>
	<!-- lion -->
	<dependency>
	     <groupId>com.dianping.lion</groupId>
	     <artifactId>lion-client</artifactId>
	     <version>0.4.6</version>
	</dependency>
	<dependency>
	    <groupId>javax.servlet</groupId>
	    <artifactId>servlet-api</artifactId>
	    <version>2.5</version>
	</dependency>
	<!-- 监控 -->
	<dependency>
	     <groupId>com.dianping.cat</groupId>
	     <artifactId>cat-core</artifactId>
	     <version>1.2.1</version>
	</dependency>
	<dependency>
	     <groupId>com.dianping.hawk</groupId>
	     <artifactId>hawk-client</artifactId>
	     <version>0.7.1</version>
	</dependency>
	<!-- 远程调用Pigeon -->
	<dependency>
	     <groupId>com.dianping.dpsf</groupId>
	     <artifactId>dpsf-net</artifactId>
	     <version>2.3.13</version>
	</dependency>

### 在Spring中配置使用
* swallow-producerclient的版本可以在[mvn repo](http://mvn.dianpingoa.com/webapp/home.html)查询所有的发行版本。本例中使用0.6.10版本。

##### Spring配置文件applicationContext-producer.xml配置相关bean

	<bean id="producerFactory" class="com.dianping.swallow.producer.impl.ProducerFactoryImpl" factory-method="getInstance" />

	<bean id="producerClient" factory-bean="producerFactory" factory-method="createProducer">
	    <constructor-arg>
	        <ref bean="destination" />
	    </constructor-arg>
	    <constructor-arg>
	        <ref bean="producerConfig" />
	    </constructor-arg>
	</bean>

	<bean id="destination" class="com.dianping.swallow.common.message.Destination" factory-method="topic">
	    <constructor-arg value="example" />
	</bean>

	<bean id="producerConfig" class="com.dianping.swallow.producer.ProducerConfig">
	    <property name="mode" value="SYNC_MODE" />
	    <property name="syncRetryTimes" value="0" />
	    <property name="zipped" value="false" />
	    <property name="threadPoolSize" value="5" />
	    <property name="sendMsgLeftLastSession" value="false" />
	</bean>

##### 使用Spring中配置的bean发送消息

	import org.springframework.context.ApplicationContext;
	import org.springframework.context.support.ClassPathXmlApplicationContext;
	import com.dianping.swallow.common.producer.exceptions.SendFailedException;
	import com.dianping.swallow.producer.Producer;

	public class ProducerSpring {
	    public static void main(String[] args) {
	    ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "applicationContext-producer.xml" });
	    Producer producer = (Producer) ctx.getBean("producerClient");
	        try {
	            System.out.println(producer.sendMessage("Hello world.") + "hello");
	        } catch (SendFailedException e) {
	            e.printStackTrace();
	        }
	    }   
	}

### 生产者端纯代码实现

纯代码实现与使用Spring配置bean有一样的效果。

	public class SyncProducerExample {
		public static void main(String[] args) throws Exception {
			producerConfig config = new ProducerConfig(); 
			// 以下设置的值与默认配置一致，可以省略
			config.setMode(ProducerMode.SYNC_MODE); 
			config.setSyncRetryTimes(0);
			config.setZipped(false);
			config.setThreadPoolSize(5);
			config.setSendMsgLeftLastSession(false);
			Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("example"), config); 
			for (int i = 0; i < 10; i++) {
				String msg = "消息-" + i;
				p.sendMessage(msg); 
				System.out.println("Sended msg:" + msg);
				Thread.sleep(500);
			}
		}
	}

### Producer配置信息详解

* mode表示producer表示工作模式。
* asyncRetryTimes表示异步模式下发送失败重试次数。
* syncRetryTimes表示同步模式下发送失败重试次数。
* zipped表示是否对待发送消息进行压缩。当消息量很大时可以设置此标志，将消息压缩后再发送。
* threadPoolSize表示异步模式时，线程池大小。默认值是1，如果设置成多线程，那么会有多个线程同时从FileQueue获取消息并发送，这样的话发送的消息就无法保证其先后顺序。
* sendMsgLeftLastSession表示异步模式时，是否重启续传。

<table class="table table-bordered table-striped table-condensed" >
   <tr>
      <td>&#23646;&#24615;</td>
      <td> &#40664;&#35748;&#20540;</td>
   </tr>
   <tr>
      <td>mode </td>
      <td>ProducerMode.ASYNC_MODE</td>
   </tr>
   <tr>
      <td>asyncRetryTimes </td>
      <td>10</td>
   </tr>
   <tr>
      <td>syncRetryTimes </td>
      <td>0</td>
   </tr>
   <tr>
      <td>zipped </td>
      <td>false</td>
   </tr>
   <tr>
      <td>threadPoolSize </td>
      <td>1</td>
   </tr>
   <tr>
      <td>sendMsgLeftLastSession </td>
      <td>true</td>
   </tr>
</table>
     
* Producer唯一定义了发送消息的方法sendMessage,下图列出了不同版本的sendMessage。对于需要发送的消息，如果是String类型，则直接发送；如果是其他类型则会被序列化为json字符串进行传输。开发时需要注意：
 
 	* 请确保content对象的类型具有默认构造方法。<br>
 	* 尽量保证content对象是简单的类型(如String/基本类型包装类/POJO)。如果content是复杂的类型，建议在您的项目上线之前，在接收消息端做测试，验证是否能够将content正常反序列化。
 
<table class= "table table-bordered table-striped table-condensed">
   <tr>
      <td>&#26041;&#27861;</td>
      <td>&#25551;&#36848;</td>
   </tr>
   <tr>
      <td>String sendMessage(Object content)</td>
      <td>content&#20026;&#21457;&#36865;&#30340;&#28040;&#24687;</td>
   </tr>
   <tr>
      <td>String sendMessage(Object content,String messageType) </td>
      <td>messageType&#29992;&#20110;&#25351;&#23450;&#36807;&#28388;&#30340;&#28040;&#24687;&#31867;&#22411;</td>
   </tr>
   <tr>
      <td>String sendMessage(Object content, Map<String,String> properties)</td>
      <td>properties&#25351;&#23450;&#28040;&#24687;&#23646;&#24615;</td>
   </tr>
   <tr>
      <td>String sendMessage(Object content, Map<String, String> properties, String messageType)</td>
      <td>&#21516;&#26102;&#25351;&#23450;&#36807;&#28388;&#30340;&#28040;&#24687;&#31867;&#22411;&#21644;&#28040;&#24687;&#23646;&#24615;</td>
   </tr>
</table>


## 使用Swallow接收消息

### Maven pox.xml中添加依赖
	<dependency>
	    <groupId>org.springframework</groupId>
	    <artifactId>spring-beans</artifactId>
	    <version>4.1.3.RELEASE</version>
	</dependency>
	<dependency>
	    <groupId>org.springframework</groupId>
	    <artifactId>spring-context</artifactId>
	    <version>4.1.3.RELEASE</version>
	</dependency>
	<dependency>
	    <groupId>org.springframework</groupId>
	    <artifactId>spring-core</artifactId>
	    <version>4.1.3.RELEASE</version>
	</dependency>
	<dependency>
	    <groupId>com.dianping.swallow</groupId>
	    <artifactId>swallow-consumerclient</artifactId>
	    <version>0.6.10</version> 
	</dependency>
	<!-- lion -->
	<dependency>
	     <groupId>com.dianping.lion</groupId>
	     <artifactId>lion-client</artifactId>
	     <version>0.4.6</version>
	</dependency>
	<!-- 监控 -->
	<dependency>
	     <groupId>com.dianping.cat</groupId>
	     <artifactId>cat-core</artifactId>
	     <version>1.2.1</version>
	</dependency>
	<dependency>
	     <groupId>com.dianping.hawk</groupId>
	     <artifactId>hawk-client</artifactId>
	     <version>0.7.1</version>
	</dependency>
* swallow-consumerclient的版本可以在[mvn repo](http://mvn.dianpingoa.com/webapp/home.html)查询所有的发行版本。本例中使用0.6.10版本。

### Spring中配置实现
#### Spring配置文件applicationContext-consumer.xml配置相关bean

	<!-- 消费者工厂类 -->
	<bean id="consumerFactory" class="com.dianping.swallow.consumer.impl.ConsumerFactoryImpl" factory-method="getInstance" />
	<!-- 消费者配置类 -->
	<bean id="consumerConfig" class="com.dianping.swallow.consumer.ConsumerConfig">
	</bean>
	<!-- 消息的目的地(即Topic) -->
	<bean id="dest" class="com.dianping.swallow.common.message.Destination" factory-method="topic">
	    <constructor-arg>
	        <value>example</value>  
	    </constructor-arg>
	</bean>
	<!-- MessageListener为您实现的消息事件监听器，负责处理接收到的消息 -->
	<bean id="messageListener" class="com.dianping.swallow.example.consumer.spring.listener.MessageListenerImpl" />
	<!-- 消费者 -->
	<bean id="consumerClient" factory-bean="consumerFactory" factory-method="createConsumer" init-method="start" destroy-method="close">
	    <constructor-arg>
	        <ref bean="dest" />
	    </constructor-arg>
	    <constructor-arg>
	        <value>xx</value>   
	    </constructor-arg>
	    <constructor-arg>
	        <ref bean="consumerConfig" />
	    </constructor-arg>
	    <property name="listener">
	        <ref local="messageListener" />
	    </property>
	</bean>

消息目的地的值example为消息种类，必须是在服务器白名单中的消息种类才能够连接服务器，否则会拒绝连接。如何申请参加[申请Topic](#topic) 
messageListener要自己实现``com.dianping.swallow.consumer.MessageListener``接口。下面列出MessageListenerImpl的实现供参考。

	package com.dianping.swallow.example.consumer.spring.listener;

	import com.dianping.swallow.common.message.Message;
	import com.dianping.swallow.consumer.MessageListener;

	public class MessageListenerImpl implements MessageListener {

		@Override
		public void onMessage(Message swallowMessage) {

			System.out.println(swallowMessage.getMessageId() + ":" + swallowMessage.getContent()+ ":" + swallowMessage.getType());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

#### Spring代码


	package com.dianping.swallow.example.consumer.spring;

	import org.springframework.context.ApplicationContext;
	import org.springframework.context.support.ClassPathXmlApplicationContext;

	import com.dianping.swallow.consumer.Consumer;

	public class TestConsumer {

		public static void main(String[] args) throws InterruptedException {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "applicationContext-consumer.xml" });
			final Consumer consumerClient = (Consumer) ctx.getBean("consumerClient");  
			consumerClient.start();
		}
	}

### 消费者端纯代码实现

	public class DurableConsumerExample {
	    public static void main(String[] args) {
	        ConsumerConfig config = new ConsumerConfig();  
	        //以下根据自己情况而定，默认是不需要配的
	        config.setThreadPoolSize(1);  
	        Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic("example"), "myId", config);  
	        c.setListener(new MessageListener() {  
	            @Override
	            public void onMessage(Message msg) {
	                System.out.println(msg.getContent());
	            }
	        });
	        c.start();  //(5)
	    }
	}

### ConsumerConfig配置详解

使用Swallow接收消息时，首先需要对接收端进行配置，这由ConsumerConfig完成:

* threadPoolSize：consumer处理消息的线程池线程数，默认为1。Consumer接收到消息时，会调用用户实现的onMessage方法。默认情况下，Consumer内部使用单线程来调用，只有onMessage执行完并响应给服务器（即发送ack给服务器），服务器在收到ack后，才会推送下一个消息过来。**如果希望并行地处理更多消息，可以通过设置threadPoolSize，实现多线程接收消息，但是如此一来，消息的时序则无法保证**
* messageFilter：consumer只消费“Message.type属性包含在指定集合中”的消息
* consumerType：consumer的类型，目前支持2种类型：
	* DURABLE_AT_LEAST_ONCE：保证消息最少消费一次，不出现消息丢失的情况。
	* NON_DURABLE：临时的消费类型，从当前的消息开始消费，不会对消费状态进行持久化，Server重启后将重新开始
* delayBaseOnBackoutMessageException：当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，2次重试之间最小的停顿时间。
* delayUpperboundOnBackoutMessageException：当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，2次重试之间最大的停顿时间。
* retryCountOnBackoutMessageException：当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，最多重试的次数。
* startMessageId表示当需要在建立连接的时候指定读取消息的位置，可以设置该参数指定。
     
<table  class= "table table-bordered table-striped table-condensed">
   <tr>
      <td>&#23646;&#24615;</td>
      <td>&#40664;&#35748;&#20540;</td>
   </tr>
   <tr>
      <td>threadPoolSize </td>
      <td>1</td>
   </tr>
   <tr>
      <td>messageFilter</td>
      <td>MessageFilter.AllMatchFilter</td>
   </tr>
   <tr>
      <td>consumerType</td>
      <td>ConsumerType.DURABLE_AT_LEAST_ONCE</td>
   </tr>
   <tr>
      <td>delayBaseOnBackoutMessageException</td>
      <td>100ms</td>
   </tr>
   <tr>
      <td>delayUpperboundOnBackoutMessageException</td>
      <td>3000ms</td>
   </tr>
   <tr>
      <td>retryCountOnBackoutMessageException</td>
      <td>5</td>
   </tr>
   <tr>
      <td>startMessageId</td>
      <td>-1</td>
   </tr>
</table>


# Swallow Web使用说明

## Topic查询

### 根据Topic名称查询

* 在左侧搜索栏里输入所要查询的topic名称，系统会提示可以搜索到的与用户关联的topic，如果提示没有返回任何内容，则说明用户没有权限查询任何topic。

* 对于每个topic，管理员首先需要添加至少一名topic的申请人，授权其访问topic的权限，得到相应权限的申请人可以根据需要添加或者删除其他topic关联人。

### 根据申请人和申请人部门查询

* 在右侧搜索栏中输入申请人或者申请人部门，系统会返回相关的提示，如果没有提示信息，则说明没有相关的查询结果。

## Message查询与重发

### 查询Topic下所有Message

* 在左侧搜索栏里输入查询message所属的topic名称，系统会提示可以搜索到的与用户关联的topic，如果提示没有返回任何内容，则说明用户没有权限查询任何topic的message信息。

### 根据Message ID精确查询

* topic确定的前提下，在右侧搜索栏中输入message ID可精确查询相应message。

### 根据保存时间精确查询

* topic确定的前提下，选择开始时间和结束时间可以查询出特定时间段发送的所有message。

### Message重发

#### 重发已保存的message

* 对于已经存在于mongo中的消息，用户可以根据查询返回结果选择需要重发的message。

* 对于重新发送的消息，其原始ID不为空，原始ID为重发消息的消息ID。

#### 重发自定义message

* 对于不存在于mongo中的message，用户可以使用发送自定义消息功能批量发送message。批量发送时，用户只需在文本框中输入消息的内容，其中每行代表一条消息。

* 用户需要对所发的自定义消息负责，swallow web不检查消息内容的格式。

### 查看Swallow消息

* 如果需要查看消息的详细内容，请点击更多选项，将会列出json格式的swallow消息内容，其中键值_id 表示保存消息的时间戳，c 表示消息体，v 表示swallow版本，s 表示消息体的sha1值， gt 表示消息的产生时间， p 表示用户设置的消息属性， _p 表示 swallow设置的消息内部属性， si 表示产生消息的主机ip。

## Topic监控和Swallow Server监控

### Topic监控

#### 监控时间段划分

* 延时监控中监控时间被分为3段：依次为用户发送-存储延时(message存储到mongo与用户发出message的时间差值)，存储-发送延迟(swallow发出message与message存储到mongo的时间差值)，发送-ack延迟(swallow收到用户ack确认与swallow发出message的时间差值)。

* topic监控分为延时监控，消息量监控和堆积量监控。
![topic-consumer延时统计](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/13.png)

#### 延时监控

* 在搜索栏中输入所要查询的topic，系统会返回topic与每个consumer在不同时间段的延时统计结果。
![topic-consumer延时统计](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/8.png)

#### 消息量监控

* 消息量监控分别从发送端，swallow端和消费端进行统计分析。用户发送频率统计每秒钟用户发送的消息数目(图表中会显示每30秒钟的发送频率)，swallow发送频率统计每秒钟swallow发送的消息数目，用户返回ack频率统计每秒钟用户返回ack的消息数目。在某一时间段，如果系统一切工作正常，应该有消费者发送频率 = swallow发送频率 = 用户返回ack频率。如果出现不相等，请对比其他消费者是否正常，如果正常，则请查看客户端代码是否正确实现了功能。
![每秒消息](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/9.png)

* 如果只想查看某一端的每秒钟统计量，只需点击右侧的图例即可切换显示和隐藏。
![每秒消息](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/10.png)

#### 堆积量监控

* 堆积量表示某一时间段堆积在数据库中没有发送给消费者的消息数目。系统会列出topic所有消费者的堆积量统计值。

*如果客户端工作正常并且及时处理消息,则不会出现消息堆积现象。
![每秒消息](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/14.png)

### Swallow Server监控

#### producer server监控

* producer server统计用户发送频率，即每秒钟发动到producer server的消息数目。
![producer server监控](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/11.png)

#### consumer server监控

* consumer server统计swallow发送频率和用户返回ack频率。正常情况下，swallow发送频率应该等于用户ack的频率。如果对于只有一个消费者的topic，理论上在消息正常发送收取时，用户发送频率应该等于swallow发送频率。如果消费者不只一个，那么swallow发送频率是同一个topic下的message发送给所有消费者数目之和。
![consumer server监控](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/12.png)

## 权限管理

### 管理员行为

* 管理员可以添加删除管理员名单的权限。

* 管理员可以可以编辑topic关联人员名单。

* 管理员可以查看访问swallow web的来访者信息。

### 用户行为

* 用户表示至少关联一个topic,可以访问关联topic的所有message的人员。

### Visitor行为

* Visitor表示没有关联任何topic,无法访问任何topic及其message的人员，只可以查看监控性能。

### 权限提升

* 用户和Visitor如需提升权限，请联系运维 jiaxing.fan@dianping.com。
     

# Swallow常见问题以及处理

## 如何查看我的消费是否有延迟、延迟多少条消息？

### Cat端追踪

* 从[CAT](http://cat.dp/)中查看Swallow项目的Transaction，可以获得相应的信息（[传送门](http://cat.dp/cat/r/t?op=view&ampdomain=Swallow)）。
* 以dp\_action这个topic为例（仅作示例，具体到自己的topic，请做相应变通），先找到In:dp_action这个type：
![Swallow Transaction In CAT](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/_layouts/15/guestaccess.aspx?guestaccesstoken=nNZxCU0aNpSP9auXeT5wSL2YMPMj63HXrNkVSUwks6I%3d&docid=0fce2e89b5fc04bfe8138dcb41716deb0)
* 上图右边对应的是当前该topic的producer生产的消息总量，点击In:dp_action链接，可以看到每个producer产生的消息数量：
![Producer Count In CAT](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/_layouts/15/guestaccess.aspx?guestaccesstoken=XQqBt9NYDQhqw6Ih7psDwNkXp4hHAfsURXavZsbuQj0%3d&docid=0d5352d0f28964e648afa31103baebd05)
* 返回上一级，在同一级页面中，找到Out:dp_action这个type，对应从consumer server发出的消息数量：
![Producer Count In CAT](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/_layouts/15/guestaccess.aspx?guestaccesstoken=x%2bbjCrZxxwIL%2bx5a529qLRCkKQPGkrVC%2f6DoqS1N3Mg%3d&docid=09b664c69bc1f42d2a76975e4c82e4dd7)
* Out:dp_action对应的数量为消费这个topic的所有consumer消费的消息总量，点击进入，可以看到每个消费者单台消费机的消费数量：
![Producer Count In CAT](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/_layouts/15/guestaccess.aspx?guestaccesstoken=pjSbpxdW21OsilhumHJpBaTGDdHicOI7Noq2YrAXYRE%3d&docid=039c404696c4942828bb4be894448236b)
* 对于一个consumer id来说，消费的消息总量，应该等于producer生产的消息总量（In:dp\_action的数量），如果消费的消息总量小于生产的消息总量，那么消费是有延迟的。

### Swallow Web端追踪

* 从Swallow Web端[延时监控](http://ppe.swallow.dp/console/monitor/consumer/total/delay)中查询出topic的延时统计数据。如果消费者出现消费延时的情况，可以查看3段延时中主要哪一段导致了消费的延迟。
* 通过查看其他消费者的发送-ack延迟是否正常，如果其他消费者消费正常，那么就需要查看客户端代码是否正确实现了功能。

## 如何查看我的Consumer消费一条消息的平均时间？

### Cat端追踪

* 从[CAT](http://cat.dp/)中查看Consumer ID对应项目的Transaction，找到MsgConsumed和MsgConsumeTried这两个type：
![Producer Count In CAT](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/_layouts/15/guestaccess.aspx?guestaccesstoken=0QH8os%2fIyyQHtz77AR5t4TzV8v6mlwQXss8n8R3kjh8%3d&docid=0426de8c2bfe84c219f857ad9a3b3b716)
* MsgConsumed表示consumer server给这个consumer推送的消息数量，MsgConsumeTried表示consumer尝试消费消息的次数，如果存在失败重试，则MsgConsumeTried数量可能会比MsgConsumed更多。
* 右边的三列可以看出consumer调用onMessage回调函数耗费的最小最大以及平均时间，如果consumer消费状况一直良好，突然某个时刻开始有消费延时，可以观察一下这里的平均时间是不是比之前更高，如果平均消费时间比正常情况高出很多，可能会造成消费延时。

### Swallow Web端追踪

* 从Swallow Web端[消息量监控](http://ppe.swallow.dp/console/monitor/consumer/total/qps)中查看用户返回ack频率，除以相应的时间段即可得到消费一条消息的平均时间。
![consumer server监控](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/15.png)

## 我的Consumer有延时，该怎么确认问题所在？
* 首先观察consumer的平均消费时间是否存在异常，如果consumer的平均消费时间比正常情况高出许多，说明onMessage回调函数依赖的服务存在问题，可以考虑_最近的代码变更_，或询问_依赖的服务_是否存在故障。
* 如果consumer的平均消费时间一直很高，说明consumer的消费线程数太少，可以考虑修改配置文件增加消费线程数，或者`扩容应用增加消费机。
* 在cat中观察consumer的problem，如果swallow相关异常过多，请及时联系swallow团队成员。
* 如果consumer的平均消费时间一直正常没有发生突变，则有可能是swallow的consumer server负载较高或存在其他故障，此时请及时联系swallow团队成员。

## 我的Consumer堵了，该怎么确认问题所在？

* 首先确认consumer是否已经正确启动：
	* 增加一些健康监测页面或其他机制以判断consumer是否正确启动。
	* 查看自己应用日志以及/data/applogs/tomcat/catalina.out日志，确认没有影响应用正常启动的异常出现。
* 确认topic是否有生产者在持续生产消息，可以参考[问题1](#_5)，连续查看swallow中的transaction，看是否存在数量变化，如果In:&lt;topic名称>没有变化，说明没有新的消息产生，而不是consumer堵住了。
* 确认consumer是否在持续消费消息，可以参考[问题2](#consumer)，连续查看consumer对应项目的transaction，看MsgConsumed这个type是否数量增加，如果这个数量在增加，说明consumer消费没有堵住。
* 其次确认是否该topic其他consumer都在消费，只有自己的consumer停止消费了。可以参考[问题1](#_5)，查看topic其他consumer的消费情况。
	* _如果该topic其他consumer也都停止消费，且生产者正常工作，请及时联系swallow团队成员_。
	* 如果该topic其他consumer消费正常，只有你自己的consumer消费堵住了，请查看consumer对应项目在CAT中的Problem，找到Heartbeat这个type，查询最新的线程堆栈，以确认Consumer的线程是否block在onMessage方法内，详细页面请参考下图：
	![Producer Count In CAT](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/_layouts/15/guestaccess.aspx?guestaccesstoken=vFNs7GHYywskM9LsejdM0Mko3IIzn8cMPhZ77JWBSEk%3d&docid=0a6b63223125843d791b57dad311a79c0)
	* 如果consumer的线程block在onMessage方法内，说明onMessage方法内调用存在异常情况，可能原因包括但不限于死循环、等待IO、死锁、数据库操作、依赖的服务超时等情况，请仔细检查这些情况，修复并重启consumer即可。
	* 如果consumer的线程不存在block现象，请及时联系swallow团队成员。

## 如何确认我的Producer正常工作？

### Cat端追踪

* 首先确认生产者是否正常启动，判别方法跟[问题4](#consumer_2)中第一点类似，增加检测页面，确保日志中没有影响正常启动的异常出现。
* 在CAT上观察Producer对应项目的transaction，找到MsgProduced以及MsgProduceTried这两个Type，MsgProduced的数量表示程序产生的消息数量，MsgProduceTried表示Swallow的producer client尝试发送给producer server的次数，如果这两个数量相差过大，说明存在异常。
![Producer Count In CAT](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/_layouts/15/guestaccess.aspx?guestaccesstoken=OE0h%2fxsUP%2b3CveKDt0t5w%2f8Gjc1jTKZqV0zmmmMfvNc%3d&docid=091aef7f093d24e04bee0c251551113f9)
* 正常情况下这两个type的数量是一一对应的，如果设置了重试，在发送失败的情况下，producer会重新尝试发送指定次数，此时MsgProduceTried的数量会大于MsgProduced的数量。如果一段时间内没有新消息发送成功，则可以认为没有新消息产生，或者Producer存在问题，此时请联系swallow团队成员。

### Swallow Web端追踪

* 从Swallow Web端[Message管理](http://ppe.swallow.dp/console/message)中查看相关topic下的message信息，检查消息是否正确发送。
![consumer server监控](https://dper-my.sharepoint.cn/personal/wenchao_meng_dianping_com/Documents/swallow/img/16.png)
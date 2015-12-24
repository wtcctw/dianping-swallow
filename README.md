注：**本文针对Swallow0.8.0版本**,下文中，${currentVersion}, currentVersion指0.8.0
使用之前，请仔细阅读[swallow使用注意事项](#takeAttention)

[TOC]

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

# Swallow系统接入流程

## 申请Topic

### 自动申请

* 通过[workflow自动申请](http://workflow.dp/wfe/start/126)
![申请topic](http://swallow.dp/help/document/img/apply_topic.png)

* 预账号请填写申请人公司邮箱前缀，如邮箱为mingdong.li@dianping.com需填写mingdong.li。

* 消息大小默认为1KB，如果大于默认值可以在下拉列表中选择接近的值，如果超出范围请邮件联系swallow开发小组。

* 每天消息量以高峰期间的值为准，如果超出所给选择范围请邮件联系swallow开发小组。

* 支付和搜索请选择相应topic类型，其余都选择一般消息队列。

* 注意区别[权限提升](#enhanceauth),此处是申请新topic，topic权限提升是提升用户对某个已经申请过的topic的访问级别。通过自动流程申请的topic，申请人自动获得查看此topic的权限。

### 邮件申请
请联系：李明冬/孟文超(mingdong.li@dianping.com, wenchao.meng@dianping.com)，待帮您配置后，方可使用（线下和线上均可以使用），未申请的topic使用时会遇到拒绝连接的异常。

联系时，**请邮件里告知：**

* 申请人所在业务部门	(例如：支付中心业务部门)
* 使用Swallow解决的业务场景是什么	(例如：订单支付成功后，使用Swallow通知xxx付款成功的消息)
* topic名称(例如：dp_action)，不能包含点(.)，只使用字母和下划线，长度不超过25个字符
* 生产者业务名，以及负责人姓名	(例如，pay-order, 林俊杰)
* 消费者业务名，以及负责人姓名  (例如，mobile-api, 陆经天)
* 计划上线时间
* 每天大概的消息量	(例如，5万条 ， 请注意不要写错，比如每日100万消息，应该写“100万”，不要写错成"100")
* 建议首先通过workflow自动申请。

# Swallow客户端使用说明
## 使用swallow发送消息
### 基本概念说明
#### Producer配置信息详解

* mode表示producer工作模式。
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
 	* sendMessage消息发送失败会抛出SendFailedException异常，可能的原因包括：网络、数据库及FileQueue故障.
 
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

### 代码示例
#### 添加maven依赖
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
	    <version>${currentVersion}</version> 
	</dependency>
	<!-- lion  请使用lion最新稳定版-->
	<dependency>
	     <groupId>com.dianping.lion</groupId>
	     <artifactId>lion-client</artifactId>
	     <version>${lion.version}</version>
	</dependency>
	<!--  如果你的应用在tomcat中启动，则此依赖无必要，否则，请添加
	<dependency>
	    <groupId>javax.servlet</groupId>
	    <artifactId>servlet-api</artifactId>
	    <version>2.5</version>
	</dependency>
	-->
	<!-- 监控  请使用cat最新稳定版-->
	<dependency>
	     <groupId>com.dianping.cat</groupId>
	     <artifactId>cat-core</artifactId>
	     <version>${cat.version}</version>
	</dependency>
	<!-- 远程调用Pigeon  请使用pigeon最新版本-->
	<dependency>
	     <groupId>com.dianping.dpsf</groupId>
	     <artifactId>dpsf-net</artifactId>
	     <version>${pigeon.version}</version>
	</dependency>


#### 生产者端代码实现


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
				try{
					p.sendMessage(msg); 
					System.out.println("Sended msg:" + msg);
				}catch(SendFailedException e){
					System.out.println("Catch exception then do what you want to do.");
				}
			}
		}
	}

## 使用Swallow接收消息
### 基本概念
<span id="consumerConfig"></span>
#### ConsumerConfig配置详解

使用Swallow接收消息时，首先需要对接收端进行配置，这由ConsumerConfig完成:


* threadPoolSize：consumer处理消息的线程池线程数，默认为1。Consumer接收到消息时，会调用用户实现的onMessage方法。默认情况下，Consumer内部使用单线程来调用，只有onMessage执行完并响应给服务器（即发送ack给服务器），服务器在收到ack后，才会推送下一个消息过来。**如果希望并行地处理更多消息，可以通过设置threadPoolSize，实现多线程接收消息，但是如此一来，消息的时序则无法保证**
* messageFilter：consumer只消费“Message.type属性包含在指定集合中”的消息
* consumerType：consumer的类型，目前支持2种类型：
	* DURABLE_AT_LEAST_ONCE：保证消息最少消费一次，不出现消息丢失的情况。
	* NON_DURABLE：临时的消费类型，从当前的消息开始消费，不会对消费状态进行持久化，Server重启后将重新开始
* delayBaseOnBackoutMessageException：当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，2次重试之间最小的停顿时间。
* delayUpperboundOnBackoutMessageException：当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，2次重试之间最大的停顿时间。
* retryCount：当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，最多重试的次数。 *0.7.0版本新增*
* startMessageId 表示当需要在建立连接的时候指定读取消息的位置，可以设置该参数指定。
* longTaskAlertTime 当用户的onMessage业务逻辑过长时的报警时间间隔，单位毫秒  *0.7.0版本新增* 
在业务处理时间过长时，会在cat上面生成长时间Transaction提示，如下图所示：
![业务逻辑时间过长cat](http://swallow.dp/help/document/img/longtask.png)

属性|默认值
-|-
threadPoolSize | 1 
messageFilter  | MessageFilter.AllMatchFilter
consumerType   | ConsumerType.DURABLE_AT_LEAST_ONCE
delayBaseOnBackoutMessageException | 100ms
delayUpperboundOnBackoutMessageException |  3000ms
retryCount | 5
startMessageId | 1
longTaskAlertTime | 5000

#### 接收消息接口
* `com.dianping.swallow.consumer.MessageListener` 
用户实现此接口，只有在抛出``BackoutMessageException``异常时才会消息重发
* `com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener` *0.7.0版本增加*
用户实现此接口，只要onMessage抛出异常，即进行消息重发

### 代码示例
#### Maven pom.xml中添加依赖
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
	    <version>${currentVersion}</version> 
	</dependency>
    <!-- lion  请使用lion最新稳定版-->
    <dependency>
         <groupId>com.dianping.lion</groupId>
         <artifactId>lion-client</artifactId>
         <version>${lion.version}</version>
    </dependency>

    <!-- 监控  请使用cat最新稳定版-->
    <dependency>
         <groupId>com.dianping.cat</groupId>
         <artifactId>cat-core</artifactId>
         <version>${cat.version}</version>
    </dependency>

* swallow-consumerclient的版本可以在[mvn repo](http://mvn.dianpingoa.com/webapp/home.html)查询所有的发行版本。

#### 消费者实现MessageListener接口

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
				logger.error("Sleep interrupted.");
			}
		}
	}


#### 消费者端代码实现
消息目的地的值example为消息种类，必须是在服务器白名单中的消息种类才能够连接服务器，否则会拒绝连接。如何申请参加[申请Topic](#topic) 

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

* createConsumer函数接收3个参数,其中第二个参数表示consumerId.对于默认的消费类型DURABLE_AT_LEAST_ONCE,必需提供一个consumerId;如果消费类型为NON_DURABLE,则不需要设置consumerId.这里的"myId"即为消费者的consumerId.

### 接收NuclearMQ消息
#### 申请权限

申请权限包括申请appKey以及申请订阅某topic的消息，请联系北京的闫志强(yanzhiqiang02)，抄送 ：程真强(chengzhenqiang) ，黄斌强(huangbinqiang)，岳小均（yuexiaojun）,王延宾（wangyanbin）。
申请时appKey使用com.dianping.swallow.开头，比如应用为swallow-test，使用com.dianping.swallow.swallow-test为appKey进行申请。
有了相关的权限可以去nuclearmq管理端（线下环境 http://release.mtmq.test.sankuai.info/ 线上环境 http://mtmq.sankuai.com/ ）自行申请订阅某topic消息。

#### 基本概念
<span id="nuclearConsumerConfig"></span>
##### NuclearConsumerConfig配置详解

使用Swallow接收NuclearMQ消息时，需要对接收端进行配置。目前只有参数delayBaseOnBackoutMessageException、delayUpperboundOnBackoutMessageException、retryCount、longTaskAlertTime、isAsync
其中只有isAsync跟接收swallow消息的ConsumerConfig不一样。isAsync是接受端使用同步还是异步方式，true为异步，false为同步，默认为false。

##### 接收消息接口

这个跟接收Swallow消息的接口一样，没有变化。

#### 代码示例
##### Maven pom.xml中添加依赖

除了依赖接收Swallow消息的pom.xml中的依赖，还有以下依赖

	 <dependency>
		  <groupId>com.dianping.swallow</groupId>
		  <artifactId>swallow-common-nuclear</artifactId>
		  <version>${currentVersion}</version>
		  </dependency>
	 <dependency>
		  <groupId>com.dianping.swallow</groupId>
		  <artifactId>swallow-consumerclient-nuclear</artifactId>
		  <version>${currentVersion}</version>
	 </dependency>

##### 消费者实现MessageListener接口

onMessage接口方法没有变，但获取的消息是BytesMessage，目前只有两个数据项，messageId、bytesContent，其中bytesContent是个byte数组，需要业务自己处理。
		
		@Override
        public void onMessage(Message msg) throws BackoutMessageException {
             BytesMessage byteMsg = (BytesMessage) msg;
             byte[] content = byteMsg.getBytesContent();
             long messageId = byteMsg.getMessageId();
        }
        
##### 消费者端代码实现

使用上跟接收Swallow消息代码风格是一样的，按照以下步骤：

*  创建NuclearConsumerFactory，Factory有两个参数appKey和isOnline。默认appKey是从META-INF/app.properties读取app.name的值，建议自己设置；isOnline设置的是环境，false表示线下，true线上环境，上海的环境alpha，beta，ppe对应北京的线下环境，product对应线上环境，因此isOnline默认值是Env.isProduct（alpha、beta、ppe为false，product为true）。注意:在使用时appKey只需填写申请时com.dianping.swallow.后面的部分，前面com.dianping.swallow.由swallow自动加上。
*  创建NuclearDestination，如下例，test_for_shanghai1为订阅的topic。
*  填写consumerId，consumerId必须填写。
*  创建Consumer，其中NuclearConsumerConfig参数。[NuclearConsumerConfig配置详解](#nuclearConsumerConfig)。
*  注册监听consumer.setListener()。
*  开始消费consumer.start()。

        public void consume() {
            ConsumerFactory consumerFactory = new NuclearConsumerFactory(true);
                Destination dest = NuclearDestination.topic("test_for_shanghai1");
                String consumerId = "com.dianping.swallow.swallow-test.test_for_shanghai1.d0";
                Consumer consumer = consumerFactory.createConsumer(dest, consumerId, new NuclearConsumerConfig(true));
                consumer.setListener(new MessageListener() {
                    @Override
                    public void onMessage(Message msg) throws BackoutMessageException {
                        BytesMessage byteMsg = (BytesMessage) msg;
                        byte[] content = byteMsg.getBytesContent();
                        long messageId = byteMsg.getMessageId();
                    }
                });
            consumer.start();
        }



# Swallow Web使用说明

各环境地址:

*  alpha：http://alpha.swallow.dp/
默认密码：123456
*  beta：http://beta.swallow.dp/
默认密码：123456
*  ppe：http://ppe.swallow.dp/
默认密码：112233
*  线上：http://swallow.dp/

## Topic查询

### 根据Topic名称查询

* 在左侧搜索栏里输入所要查询的topic名称，系统会提示可以搜索到的与用户关联的topic，如果提示没有返回任何内容，则说明用户没有权限查询任何topic。

* 对于每个topic，默认情况下除了管理员,其他人是没有权限查看topic下的message.如果有相应的需求,可以通过[workflow](http://workflow.dp/wfe/start/119)申请相关topic的权限.


## Message查询与重发

### 查询Topic下所有Message

* 在左侧搜索栏里输入查询message所属的topic名称，系统会提示可以搜索到的与用户关联的topic，如果提示没有返回任何内容，则说明用户没有权限查询任何topic的message信息。

### 根据Message ID精确查询

* topic确定的前提下，在右侧搜索栏中输入message ID可精确查询相应message。

### 根据保存时间精确查询

* topic确定的前提下，选择开始时间和结束时间可以查询出特定时间段发送的所有message。尽量缩小查找的时间段，减轻查询的时间开销。

### 导出消息

* 根据时间查询的消息可以导出到文件并且下载到本地。消息导出后页面会自动跳转到下载页，点击链接即可下载文件。如果导出的数据量很大，则需要一定的时间等待任务执行完成。

* 在消息大小不超过1KB的前提下，允许导出的最多100万条数据，如果消息大小大于1KB，则最多只能导出1G大小的消息。

* 导出的消息保存7天后自动从磁盘删除。

### Message重发

#### web端重发已保存的message

* 对于已经存在于mongo中的消息，用户可以根据查询返回结果选择需要重发的message。

* 对于重新发送的消息，其原始ID不为空，原始ID为重发消息的消息ID。

#### 通过api重发已保存的message

* 联系运维人员获得重发消息的认证字符串(AuthenticationString)，该字符串每天更新一次。

* api接口为{swallow.web.sso.url}/api/message/sendmessageid，通过读取lion获得key为swallow.web.sso.url的值。也可根据运行环境固定服务器，下表列出swallow.web.sso.url在不同环境下值。

环境|swallow.web.sso.url
-|-
alpha | http://alpha.swallow.dp 
beta  | http://beta.swallow.dp
ppe   | http://ppe.swallow.dp
product | http://swallow.dp


* 发送post请求，需要3个参数，topic(topic名称)，mid(消息ID)和authentication(认证字符串)。

* 返回值为json字符串，包含2个键值对，status(状态码)，message(状态码对应的消息)。下表列出了不同状态码表示的意义。

	* 0表示操作成功。
	* 负的状态码表示不可重试的错误。
	* 正的状态码表示可以重试成功的错误。 

状态码|消息
-|-
-4 | empty content
-3  | no authenticaton
-2   | unauthorized
-1 | write mongo error
0 | success
1 | read time out


#### 使用示范

	import org.codehaus.jettison.json.JSONException;
	import org.codehaus.jettison.json.JSONObject;
	import org.apache.commons.httpclient.HttpClient;
	import org.apache.commons.httpclient.HttpMethod;
	import org.apache.commons.httpclient.NameValuePair;
	import org.apache.commons.httpclient.methods.PostMethod;
	private HttpMethod postMethod(String url) throws IOException {
		PostMethod post = new PostMethod(url);
		NameValuePair[] param = {
				new NameValuePair("mid", "6161611639629021185"),
				new NameValuePair("authentication", "lfimuqqxjlgvniueuiqooorkkyxdmwrm"),
				new NameValuePair("topic", "example") }; //topic名称
		post.setRequestBody(param);  //设置消息体
		post.releaseConnection();
		return post;
	}
	public static void main(String[] args) {
		String host = null;
		try {
			ConfigCache configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
			host = configCache.getProperty("swallow.web.sso.url");
		} catch (LionException e) {
			logger.error("Error when using lion.", e);
		}
		String url = host + "/api/message/sendmessageid";
		HttpClient httpClient = new HttpClient();
		try {
			HttpMethod method = postMethod(url);
			httpClient.executeMethod(method);

			String response = method.getResponseBodyAsString();
			try {
				JSONObject json = new JSONObject(response);
				System.out.println(json.getInt("status"));
				System.out.println(json.getString("message"));
			} catch (JSONException e) {
				logger.error("Error when parse json", e);
			}
		} catch (IOException e) {
			logger.error("Error when execute http request.", e);
		}
	}


#### 重发自定义message

* 对于不存在于mongo中的message，用户可以使用发送自定义消息功能批量发送message。批量发送时，如果消息有类型则输入相应的类型，否则可忽略；如果需要添加消息属性，可以输入形式如key1:value1,key2:value2的键值对字符串，分隔符有5个不同的选择，分别为':'，';'，','，'_'和'#'。如果键值对中包含了分隔符，则可以选择其他的分隔符以示区分。如果有多个属性键值对，可以点击Add添加属性，最多支持100个属性。文本框中输入消息体的内容。

* 用户需要对所发的自定义消息负责，swallow web不检查消息内容的格式。

#### 通过api重发自定义的message

* 联系运维人员获得重发消息的认证字符串(AuthenticationString)，该字符串每天更新一次。

* api接口为{swallow.web.sso.url}/api/message/sendmessage，通过读取lion获得key为swallow.web.sso.url的值。

* 发送post请求，5个必选参数，topic(topic名称)，type(消息类型)，property(消息属性)，authentication(认证字符串)，content(消息体内容)和一个可选参数delimitor(property键值对分隔符)。

	* property键值对默认使用':'作为分隔符，如果用户的键值中有':'，可以在可选参数delimitor中设置分隔符字符串。

	* content字符串，表示消息内容。

* 返回值为json字符串，包含2个键值对，status(状态码)，message(状态码对应的消息)，与通过api重发已保存的message返回值意义一致。


#### 使用示范

	private HttpMethod postMethod(String url) throws IOException{
		PostMethod post = new PostMethod(url);
		String contents = "test group message api with type and property, No 1";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		nameValuePairs.add(new NameValuePair("content",contents));
		nameValuePairs.add(new NameValuePair("topic", "example"));
		nameValuePairs.add(new NameValuePair("type", "jiagou"));
		nameValuePairs.add(new NameValuePair("property", "test:true::work:on"));  //::用于分割多个键值对
		nameValuePairs.add(new NameValuePair("authentication", "lfimuqqxjlgvniueuiqooorkkyxdmwrm"));

		NameValuePair[] array = new NameValuePair[nameValuePairs.size()];
		nameValuePairs.toArray(array);
		post.setRequestBody(array);  //设置消息体
		post.releaseConnection();
		return post;
	}

* 与重发已保存的message相似，只需更改请求url和post参数。

* 请求url: String url = host + "/api/message/sendmessage"。


### 查看Swallow消息

* 如果需要查看消息的详细内容，请点击更多选项，将会列出json格式的swallow消息内容，其中键值_id 表示保存消息的时间戳，c 表示消息体，v 表示swallow版本，s 表示消息体的sha1值， gt 表示消息的产生时间， p 表示用户设置的消息属性， _p 表示 swallow设置的消息内部属性， si 表示产生消息的主机ip。

## Topic监控和Swallow Server监控

### Topic监控

#### 基础概念

#### 延时监控

<span id="webDelay"></span>
延时监控中监控时间被分为3段

1. 用户发送-存储延时(message存储到mongo与用户发出message的时间差值)
1. 存储-发送延迟(swallow发出message与message存储到mongo的时间差值)
1. 发送-ack延迟(swallow收到用户ack确认与swallow发出message的时间差值)

![topic-consumer延时统计](http://swallow.dp/help/document/img/13.png)

* 在搜索栏中输入所要查询的topic，系统会返回topic与每个consumer在不同时间段的延时统计结果。
![topic-consumer延时统计](http://swallow.dp/help/document/img/8.png)

#### 每秒消息(QPS)监控

* 消息量监控从swallow服务器端统计消息发送频率(QPS)，分为三段进行统计，和上述延时分段对应。

swallow发送频率统计每秒钟swallow发送的消息数目，用户返回ack频率统计每秒钟用户返回ack的消息数目。在某一时间段，如果系统一切工作正常，对应特定的consumerID，应该有消费者发送频率 = swallow发送频率 = 用户返回ack频率。如果出现不相等，请对比其他消费者是否正常，如果正常，则请查看客户端代码是否正确实现了功能。
![每秒消息](http://swallow.dp/help/document/img/9.png)

* 如果只想查看某一端的每秒钟统计量，只需点击右侧的图例即可切换显示和隐藏。
![每秒消息](http://swallow.dp/help/document/img/10.png)

#### 堆积量监控

* 堆积量表示某一时间段堆积在数据库中没有发送给消费者的消息数目。系统会列出topic所有消费者的堆积量统计值。

* 如果客户端工作正常并且及时处理消息,消息堆积将会在一定数值范围内波动。
![每秒消息](http://swallow.dp/help/document/img/14.png)

### Swallow Server监控

#### producer server监控

* producer server统计用户发送频率，即每秒钟发动到producer server的消息数目。
![producer server监控](http://swallow.dp/help/document/img/11.png)

#### consumer server监控

* consumer server统计swallow发送频率和用户返回ack频率。正常情况下，swallow发送频率应该等于用户ack的频率。如果对于只有一个消费者的topic，理论上在消息正常发送收取时，用户发送频率应该等于swallow发送频率。如果消费者不只一个，那么swallow发送频率是同一个topic下的message发送给所有消费者数目之和。
![consumer server监控](http://swallow.dp/help/document/img/12.png)

## Swallow 告警

### server告警（针对swallow研发团队）

1.	producer server服务告警

	服务告警目前只检测server pigeon服务的健康检测页面和发往web端的统计数据两个方面，四种告警类型：

	* [1]PRODUCER_SERVER_PIGEON_SERVICE，pigeon服务故障。

	* [2]PRODUCER_SERVER_PIGEON_SERVICE_OK，pigeon服务故障恢复。

	* [3]PRODUCER_SERVER_SENDER，统计数据未发送。

	* [4]PRODUCER_SERVER_SENDER_OK，统计数据发送故障恢复。

2. 	producer server统计数据告警

	服务告警目前只检测某台server QPS 峰值、谷值两个方面，四种告警类型：

	* [5]PRODUCER_SERVER_QPS_PEAK，QPS超过峰值。

	* [6]PRODUCER_SERVER_QPS_VALLEY，QPS低于谷值。

	* [8]PRODUCER_SERVER_QPS_OK，QPS恢复正常。

3.	consumer server服务告警

	服务告警目前只检测server 端口是否打开，发往web端的统计数据以及slave服务器服务是否开启三个方面，八种告警类型：

	* [9]CONSUMER_SERVER_SENDER，统计数据未发送。

	* [10]CONSUMER_SERVER_SENDER_OK，统计数据发送故障恢复。

	* [11]CONSUMER_SERVER_SLAVEPORT_OPENED，Master端口关闭，Slave端口打开。

	* [12]CONSUMER_SERVER_BOTHPORT_OPENED，Master，Slave端口都打开。

	* [13]CONSUMER_SERVER_BOTHPORT_UNOPENED，Master，Slave端口都未打开。

	* [14]CONSUMER_SERVER_PORT_OPENED_OK，Master，Slave端口恢复正常状态。

	* [15]CONSUMER_SERVER_SLAVESERVICE_STARTED，Slave服务未开启。

	* [16]CONSUMER_SERVER_SLAVESERVICE_STARTED_OK，Slave服务恢复正常。

4. 	consumer server统计数据告警

	服务告警目前只检测某台server 发送和确认QPS 峰值、谷值、波动六个方面，八种告警类型：

	* [17]CONSUMER_SERVER_SENDQPS_PEAK，发送QPS超过峰值。

	* [18]CONSUMER_SERVER_SENDQPS_VALLEY，发送QPS低于谷值。

	* [19]CONSUMER_SERVER_SENDQPS_FLUCTUATION，发送QPS与历史数据相比波动过大。

	* [20]CONSUMER_SERVER_SENDQPS_OK，发送QPS恢复正常。

	* [21]CONSUMER_SERVER_ACKQPS_PEAK，确认QPS超过峰值。

	* [22]CONSUMER_SERVER_ACKQPS_VALLEY，确认QPS低于谷值。

	* [23]CONSUMER_SERVER_ACKQPS_FLUCTUATION，确认QPS与历史数据相比波动过大。

	* [24]CONSUMER_SERVER_ACKQPS_OK，确认QPS恢复正常。

5. 	producer and consumer server配置一致性告警

	服务告警目前只检测mongo的配置，有两种告警类型：

	* [25]SERVER_MONGO_CONFIG，配置与lion上的mongo配置不一致。

	* [26]SERVER_MONGO_CONFIG_OK，配置与lion上的mongo配置不一致。

### topic告警（针对消息的生产者）

1. 	topic统计数据告警

	topic告警目前只检测producer topic QPS的峰值、谷值、波动以及topic message延时四个方面，四种告警类型：

	* [1001]PRODUCER_TOPIC_QPS_PEAK，Topic QPS超过峰值（生产端发送的qps过高，超过设定的峰值）。

	* [1002]PRODUCER_TOPIC_QPS_VALLEY，Topic QPS低于谷值（生产端发送的qps过低，低于设定的谷值）。

	* [1003]PRODUCER_TOPIC_QPS_FLUCTUATION，Topic QPS与历史数据相比波动过大（生产端发送的qps与前一天这个时刻前后5分钟内qps的均值比较，超过波动基数和波动比例）。

	* [1004]PRODUCER_TOPIC_MESSAGE_DELAY，Topic message存储延时（生产端发送到server端持久化的时间差过大，大于设定的延时阈值）。

2.	topic告警分析

	对于峰值、谷值、波动、延时告警都是对系统当前可能出现异常的一个提示，都可能是程序bug引起的问题，当然也可能没有问题。

3.	告警配置

	根据业务自身的情况，可以设置对应topic的告警配置（需要有该topic的权限，若没有，可通过[workflow](http://workflow.dp/wfe/start/119)进行申请），可以修改默认的告警配置值。如下图，针对峰值、谷值、波动、波动基数、延时的参数设置。其中峰值是指Qps的最高值，大于此值报警；谷值是指Qps的最低值，小于此值报警；波动是指当前Qps和前一天此时刻前后5分钟的Qps平均值之间的倍数大小；波动基数，前面两个Qps至少有一个大于波动基数值，才进行波动比较；延时是指存储延时，是指消息发出到存入Swallow这段时间差，超过设定的值，就会告警。
	![Topic报警参数](http://swallow.dp/help/document/img/topic-alarm-setting.png)
	
	对topic告警还有开关设置，可以打开或关闭告警。如下图，对Topic告警开关，有发送告警（产生此topic消息的生产端）、接收告警（所有消费此topic的所有消费端）开关，分别针对Topic的发送端、接收端异常告警的开关。
	![Topic报警开关](http://swallow.dp/help/document/img/topic-alarm-switch.png)
	
	另外，由于swallow告警是通过消息发送，接收端的ip从cmdb拉取告警人信息，进行告警，可能某个topic相关的发送ip不想要告警，也只可以设置这个topic某个ip的开关。如下图
	![Topic ip报警开关](http://swallow.dp/help/document/img/topic-ip-alarm-switch.png)

### consumerId告警（针对消息的消费者）

1. 	consumerId 统计数据告警

	consumerId告警目前只检测某consumerId发送和确认QPS 峰值、谷值、波动以及消费累积九个方面，九种告警类型：

	* [1013]CONSUMER_CONSUMERID_SENDQPS_PEAK，ConsumerId 发送QPS超过峰值（服务端发送到消费端的qps过高，超过设定的峰值）。

	* [1014]CONSUMER_CONSUMERID_SENDQPS_VALLEY，ConsumerId 发送QPS低于谷值（服务端发送到消费端的qps过低，低于设定的谷值）。

	* [1015]CONSUMER_CONSUMERID_SENDQPS_FLUCTUATION，ConsumerId 发送QPS与历史数据相比波动过大（服务端发送到消费端的qps与前一天这个时刻前后5分钟内qps的均值比较，超过波动基数和波动比例）。

	* [1016]CONSUMER_CONSUMERID_SENDMESSAGE_DELAY，ConsumerId 发送QPS延时（从服务端从存储取消息到发送到消费端这个过程时间差过大，大于设定的延时阈值）。

	* [1017]CONSUMER_CONSUMERID_SENDMESSAGE_ACCUMULATION，ConsumerId message累积（服务端消息累积，累积量大于阈值）。

	* [1018]CONSUMER_CONSUMERID_ACKQPS_PEAK，ConsumerId 确认QPS超过峰值（消费端确认消息的qps过高，超过设定的峰值）。

	* [1019]CONSUMER_CONSUMERID_ACKQPS_VALLEY，ConsumerId 确认QPS低于谷值（消费端确认消息的qps过低，低于设定的谷值）。

	* [1020]CONSUMER_CONSUMERID_ACKQPS_FLUCTUATION，ConsumerId 确认QPS与历史数据相比波动过大（消费端确认消息的qps与前一天这个时刻前后5分钟内qps的均值比较，超过波动基数和波动比例）。

	* [1021]CONSUMER_CONSUMERID_ACKMESSAGE_DELAY，ConsumerId 确认QPS延时（从服务端发送成功到消费端确认消息的时间差过大，大于设定的延时阈值）。

2.	consumerId告警分析

	如果出现延时或者累积报警，先查看是否是消息接收端处理过慢（并发数过低、单条消息处理过慢，异常导致onMessage处理挂起），可以借助swallow管理平台上的监控和Cat监控，参见[Swallow常见问题以及处理](#commonProblems)。


3.	告警配置

	根据业务自身的情况，可以设置对应ConsumerId的告警配置（需要有相关topic的权限，若没有，可通过[workflow](http://workflow.dp/wfe/start/119)进行申请），可以修改默认的告警配置值。如下图，针对峰值、谷值、波动、波动基数、延时、累积的参数设置。这些参数根据消息消费流程分成两个过程：发送过程（swallow从存储取消息到发送消息给消费端这段过程）；Ack过程（swallow服务端发送消息到客户端消费完消息给服务端回馈这段过程）。这两段分别有对应的峰值、谷值、波动、延时。这些含义类似于Topic告警参数，详情可看Topic告警参数，其中累积是指swallow服务端消息的累积量，若大于设定的值，则报警。
    ![ConsumerId报警参数](http://swallow.dp/help/document/img/consumerId-alarm-setting.png)
    	
    对ConsumerId告警还有开关设置，可打开或关闭告警，如下图。
    ![ConsumerId报警开关](http://swallow.dp/help/document/img/consumerId-alarm-switch.png)
    
    另外，由于swallow告警是通过消息发送，接收端的ip从cmdb拉取告警人信息，进行告警，可能某个consumerId相关的消费ip不想要告警，也只可以设置这个consumerId的某个ip的开关。如下图
    ![ConsumerId ip报警开关](http://swallow.dp/help/document/img/consumerId-ip-alarm-switch.png)

### IP告警

1.	producer ip告警（针对消息的生产者）

	producer ip告警定位到生产者主机，根据统计数据判断主机是否宕机，一种告警类型：

	* [1022]PRODUCER_CLIENT_SENDER，producer client一段时间未发送消息。

2.	consumer ip告警（针对消息的消费者）

	consumer ip告警定位到消费者主机，根据统计数据判断主机是否宕机，一种告警类型：

	* [1023]CONSUMER_CLIENT_RECEIVER，consumer client一段时间未接收消息。

3.	告警配置
	
	这些告警的ip都来源于某个topic（生产端）或者某个consumerId（消费端），因此可以到topic或者consumerId处设置相关ip可是否需要告警。设置过程如topic或consumerId告警配置中设置具体ip开关。

## Swallow 大盘

### 综合大盘

* 综合大盘会显示消费者发送延迟,消费者确认延迟和消息堆积的综合统计数据.超过默认阈值的统计项会被标红.大盘会根据一分钟的统计量生成最终结果.
![综合大盘](http://swallow.dp/help/document/img/17.png)

### 发送延迟,确认延迟和堆积大盘

* 发送延迟大盘根据发送延迟统计量生成相应的大盘;确认延迟大盘根据确认延迟统计量生成相应的大盘;堆积大盘根据消息堆积量生成相应的大盘.
	   
	
## 权限管理

### 管理员行为

* 添加删除管理员名单的权限
* 编辑topic关联人员名单
* 查看访问swallow web的来访者信息
* 查看、重发消息


### 用户行为

* 每个用户管理一个或者多个topic，可以查看、重发相应的topic的消息。

### Visitor行为

* Visitor表示没有关联任何topic,无法访问任何topic及其message的人员，只可以查看监控性能。

<span id="enhanceauth"></span>
### 权限提升

* 用户和Visitor如需提升权限，可通过[workflow](http://workflow.dp/wfe/start/119)进行申请。
![权限提升](http://swallow.dp/help/document/img/enhance_auth.png)

* topic需要是已经通过swallow开发小组审批过的可用topic，否则请先申请相关topic。一次只能填写一个topic名称。

* 预账号请填写申请人公司邮箱前缀，如邮箱为mingdong.li@dianping.com需填写mingdong.li。


<span id="takeAttention"></span>
# Swallow使用注意事项


以下是业务在swallow的过程中遇到的一些常见问题，希望大家仔细阅读，避免类似的问题发生

1. swallow保证消息不丢，所以针对特定一条消息，在异常情况下(网络异常，服务器重启，用户onMessage代码挂死)，可能会重复发送。
	* 要求业务做幂等处理，尤其是支付相关的业务（当消息重复被投递的时候，多次处理结果是一样的）
    * 为了保证线上业务运行正常，同时方便大家进行消息幂等处理测试，**beta**环境的swallow会对消息进行多次重发；其它环境行为**保持不变**。
1. consumer接收消息默认并发为1，如果业务处理速度过慢，可能会出现消息堆积（假设每条消息处理时间100ms，则一秒只能处理10条消息）
	* 如果业务不要求时序，可以将并发至调高，建议并发值:50，设置方法参见[consumer配置](#consumerConfig)
1. swallow作为异步消息队列，正常情况下延时在秒级别。但是异常情况下（数据库升级等），可能会出现分钟级别的延时，如果业务对延时特别敏感，建议谨慎使用

<span id="commonProblems"></span>
# Swallow常见问题以及处理


<span id="problemWhichConsumer"></span>
## 如何查找某个topic对应的consumer
* 从[CAT](http://cat.dp)中，找到swallow项目[Swallow](http://cat.dp/cat/r/t?op=view&domain=Swallow)
* 找到`Out:topic`(topic即你关心的topic名字)，点开，即可看到consumer信息


<span id="problemDelay"></span>
## 如何查看我的消费是否有延迟、延迟多少条消息？

### Cat端追踪

* 从[CAT](http://cat.dp/)中查看Swallow项目的Transaction，可以获得相应的信息（[传送门](http://cat.dp/cat/r/t?op=view&ampdomain=Swallow)）。
* 以dp\_action这个topic为例（仅作示例，具体到自己的topic，请做相应变通），先找到In:dp_action这个type：
![Swallow Transaction In CAT](http://swallow.dp/help/document/img/1.png)
* 上图右边对应的是当前该topic的producer生产的消息总量，点击In:dp_action链接，可以看到每个producer产生的消息数量：
![Producer Count In CAT](http://swallow.dp/help/document/img/2.png)
* 返回上一级，在同一级页面中，找到Out:dp_action这个type，对应从consumer server发出的消息数量：
![Producer Count In CAT](http://swallow.dp/help/document/img/3.png)
* Out:dp_action对应的数量为消费这个topic的所有consumer消费的消息总量，点击进入，可以看到每个消费者单台消费机的消费数量：
![Producer Count In CAT](http://swallow.dp/help/document/img/4.png)
* 对于一个consumer id来说，消费的消息总量，应该等于producer生产的消息总量（In:dp\_action的数量），如果消费的消息总量小于生产的消息总量，那么消费是有延迟的。

### Swallow Web端追踪

* 从Swallow Web端[延时监控](http://swallow.dp/console/monitor/consumer/total/delay)中查询出topic的延时统计数据。如果消费者出现消费延时的情况，可以查看3段延时中主要哪一段导致了消费的延迟。
* 通过查看其他消费者的发送-ack延迟是否正常，如果其他消费者消费正常，那么就需要查看客户端代码是否正确实现了功能。

<span id="problemAverageDelay"></span>
## 如何查看我的Consumer消费一条消息的平均时间？

### Cat端追踪

* 从[CAT](http://cat.dp/)中查看Consumer ID对应项目的Transaction，找到MsgConsumed和MsgConsumeTried这两个type：
![Producer Count In CAT](http://swallow.dp/help/document/img/5.png)
* MsgConsumed表示consumer server给这个consumer推送的消息数量，MsgConsumeTried表示consumer尝试消费消息的次数，如果存在失败重试，则MsgConsumeTried数量可能会比MsgConsumed更多。
* 右边的三列可以看出consumer调用onMessage回调函数耗费的最小最大以及平均时间，如果consumer消费状况一直良好，突然某个时刻开始有消费延时，可以观察一下这里的平均时间是不是比之前更高，如果平均消费时间比正常情况高出很多，可能会造成消费延时。

### Swallow Web端追踪

* 参见[延时监控](#webDelay)中发送-ack延时


<span id="problemConsumerDelay"></span>
## 我的Consumer有延时，该怎么确认问题所在？
* 首先观察consumer的平均消费时间是否存在异常，如果consumer的平均消费时间比正常情况高出许多，说明onMessage回调函数依赖的服务存在问题，可以考虑_最近的代码变更_，或询问_依赖的服务_是否存在故障。
* 如果consumer的平均消费时间一直很高，说明consumer的消费线程数太少，可以考虑修改配置文件增加消费线程数，或者`扩容应用增加消费机。
* 在cat中观察consumer的problem，如果swallow相关异常过多，请及时联系swallow团队成员。
* 如果consumer的平均消费时间一直正常没有发生突变，则有可能是swallow的consumer server负载较高或存在其他故障，此时请及时联系swallow团队成员。

## 我的Consumer堵了，该怎么确认问题所在？
* 首先确认consumer是否已经正确启动：
	* 增加一些健康监测页面或其他机制以判断consumer是否正确启动。
	* 查看自己应用日志以及/data/applogs/tomcat/catalina.out日志，确认没有影响应用正常启动的异常出现。
* 确认topic是否有生产者在持续生产消息，连续查看swallow中的transaction，看是否存在数量变化，如果In:&lt;topic名称>没有变化，说明没有新的消息产生，而不是consumer堵住了。
* 确认consumer是否在持续消费消息，可以[参考](#problemAverageDelay)，连续查看consumer对应项目的transaction，看MsgConsumed这个type是否数量增加，如果这个数量在增加，说明consumer消费没有堵住。
* 其次确认是否该topic其他consumer都在消费，只有自己的consumer停止消费了。可以[参考](#problemDelay)，查看topic其他consumer的消费情况。
	* _如果该topic其他consumer也都停止消费，且生产者正常工作，请及时联系swallow团队成员_。
	* 如果该topic其他consumer消费正常，只有你自己的consumer消费堵住了，请查看consumer对应项目在CAT中的Problem，找到Heartbeat这个type，查询最新的线程堆栈，以确认Consumer的线程是否block在onMessage方法内，详细页面请参考下图：
	![Producer Count In CAT](http://swallow.dp/help/document/img/6.png)
	* 如果consumer的线程block在onMessage方法内，说明onMessage方法内调用存在异常情况，可能原因包括但不限于死循环、等待IO、死锁、数据库操作、依赖的服务超时等情况，请仔细检查这些情况，修复并重启consumer即可。
	* 如果consumer的线程不存在block现象，请及时联系swallow团队成员。

<span id="problemProducer"></span>
## 如何确认我的Producer正常工作？

### Cat端追踪

* 首先确认生产者是否正常启动，判别方法跟前述问题中第一点类似，增加检测页面，确保日志中没有影响正常启动的异常出现。
* 在CAT上观察Producer对应项目的transaction，找到MsgProduced以及MsgProduceTried这两个Type，MsgProduced的数量表示程序产生的消息数量，MsgProduceTried表示Swallow的producer client尝试发送给producer server的次数，如果这两个数量相差过大，说明存在异常。
![Producer Count In CAT](http://swallow.dp/help/document/img/7.png)
* 正常情况下这两个type的数量是一一对应的，如果设置了重试，在发送失败的情况下，producer会重新尝试发送指定次数，此时MsgProduceTried的数量会大于MsgProduced的数量。如果一段时间内没有新消息发送成功，则可以认为没有新消息产生，或者Producer存在问题，此时请联系swallow团队成员。

### Swallow Web端追踪

* 从Swallow Web端[Message管理](http://swallow.dp/console/message)中查看相关topic下的message信息，检查消息是否正确发送。
![consumer server监控](http://swallow.dp/help/document/img/16.png)

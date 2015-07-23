db = db.getSiblingDB('swallowwebapplication');
result = db.swallowwebalarmmetac.findOne();

if(result != null) {
	print("swallowwebalarmmetac already exist");
}else{

	print("swallowwebalarmmetac load data start");
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 1
		}, {
	    "metaId" : 1,
	    "type" : "PRODUCER_SERVER_PIGEON_SERVICE",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器服务告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}不能访问pigeon健康监测页面，可能宕机。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	},true,false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 2
		}, {
	    "metaId" : 2,
	    "type" : "PRODUCER_SERVER_SENDER",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器SENDER告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}未发送统计数据到管理端，可能宕机。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 3
		}, {
	    "metaId" : 3,
	    "type" : "PRODUCER_SERVER_QPS_PEAK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器QPS告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}[QPS]{currentValue}高于峰值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 4
		}, {
	    "metaId" : 4,
	    "type" : "PRODUCER_SERVER_QPS_VALLEY",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器QPS告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}[QPS]{currentValue}低于谷值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 5
		}, {
	    "metaId" : 5,
	    "type" : "PRODUCER_SERVER_QPS_FLUCTUATION",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器QPS告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}[QPS]{currentValue}与历史同期值{expectedValue}波动较大。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 6
		}, {
	    "metaId" : 6,
	    "type" : "PRODUCER_TOPIC_QPS_PEAK",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产端TOPIC QPS告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}[QPS]{currentValue}高于峰值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 7
		}, {
	    "metaId" : 7,
	    "type" : "PRODUCER_TOPIC_QPS_VALLEY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产端TOPIC QPS告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}[QPS]{currentValue}低于谷值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 8
		}, {
	    "metaId" : 8,
	    "type" : "PRODUCER_TOPIC_QPS_FLUCTUATION",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产端TOPIC QPS告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}[QPS]{currentValue}与历史同期值{expectedValue}波动较大。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 9
		}, {
	    "metaId" : 9,
	    "type" : "PRODUCER_TOPIC_MESSAGE_DELAY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产端TOPIC存储延时告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}存储延时{currentValue}大于阈值{expectedValue}(s)。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 10
		}, {
	    "metaId" : 10,
	    "type" : "CONSUMER_SERVER_SENDER",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器SENDER告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}未发送统计数据到管理端，可能宕机。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 11
		}, {
	    "metaId" : 11,
	    "type" : "CONSUMER_SERVER_SLAVEPORT_OPENED",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器PORT告警",
	    "alarmTemplate" : "消费服务器[MASTER IP]{masterIp}[SLAVE IP]{slaveIp}，SLAVE端口处于打开状态。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 12
		}, {
	    "metaId" : 12,
	    "type" : "CONSUMER_SERVER_BOTHPORT_OPENED",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器PORT告警",
	    "alarmTemplate" : "消费服务器[MASTER IP]{masterIp}[SLAVE IP]{slaveIp}，MASTER SLAVE端口都处于打开状态。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 13
		}, {
	    "metaId" : 13,
	    "type" : "CONSUMER_SERVER_BOTHPORT_UNOPENED",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器PORT告警",
	    "alarmTemplate" : "消费服务器[MASTER IP]{masterIp}[SLAVE IP]{slaveIp}，MASTER SLAVE端口都处于未打开状态。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 14
		}, {
	    "metaId" : 14,
	    "type" : "CONSUMER_SERVER_SENDQPS_PEAK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器发送QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送[QPS]{currentValue}高于峰值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 15
		}, {
	    "metaId" : 15,
	    "type" : "CONSUMER_SERVER_SENDQPS_VALLEY",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器发送QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送[QPS]{currentValue}低于谷值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true,  false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 16
		}, {
	    "metaId" : 16,
	    "type" : "CONSUMER_SERVER_SENDQPS_FLUCTUATION",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器发送QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送[QPS]{currentValue}与历史同期值{expectedValue}波动较大。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 17
		}, {
	    "metaId" : 17,
	    "type" : "CONSUMER_SERVER_ACKQPS_PEAK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器确认QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}确认[QPS]{currentValue}高于峰值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	},true,false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 18
		}, {
	    "metaId" : 18,
	    "type" : "CONSUMER_SERVER_ACKQPS_VALLEY",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器确认QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}确认[QPS]{currentValue}低于谷值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 19
		}, {
	    "metaId" : 19,
	    "type" : "CONSUMER_SERVER_ACKQPS_FLUCTUATION",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器确认QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}确认[QPS]{currentValue}与历史同期值{expectedValue}波动较大。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 20
		}, {
	    "metaId" : 20,
	    "type" : "CONSUMER_TOPIC_SENDQPS_PEAK",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}发送[QPS]{currentValue}高于峰值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 21
		}, {
	    "metaId" : 21,
	    "type" : "CONSUMER_TOPIC_SENDQPS_VALLEY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}发送[QPS]{currentValue}低于谷值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 22
		}, {
	    "metaId" : 22,
	    "type" : "CONSUMER_TOPIC_SENDQPS_FLUCTUATION",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}发送[QPS]{currentValue}与历史同期值{expectedValue}波动较大。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 23
		}, {
	    "metaId" : 23,
	    "type" : "CONSUMER_TOPIC_SENDMESSAGE_DELAY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC发送延时告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}发送延时{currentValue}延时大于阈值{expectedValue}(s)。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 24
		}, {
	    "metaId" : 24,
	    "type" : "CONSUMER_TOPIC_ACKQPS_PEAK",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}确认[QPS]{currentValue}高于峰值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 25
		}, {
	    "metaId" : 25,
	    "type" : "CONSUMER_TOPIC_ACKQPS_VALLEY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}确认[QPS]{currentValue}低于谷值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 26
		}, {
	    "metaId" : 26,
	    "type" : "CONSUMER_TOPIC_ACKQPS_FLUCTUATION",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}确认[QPS]{currentValue}与历史同期值{expectedValue}波动较大。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 27
		},{
	    "metaId" : 27,
	    "type" : "CONSUMER_TOPIC_ACKMESSAGE_DELAY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC确认延时告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}确认延时{currentValue}延时大于阈值{expectedValue}(s)。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 28
		},{
	    "metaId" : 28,
	    "type" : "CONSUMER_CONSUMERID_SENDQPS_PEAK",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}发送[QPS]{currentValue}高于峰值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 29
		},{
	    "metaId" : 29,
	    "type" : "CONSUMER_CONSUMERID_SENDQPS_VALLEY",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}发送[QPS]{currentValue}低于谷值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 30
		},{
	    "metaId" : 30,
	    "type" : "CONSUMER_CONSUMERID_SENDQPS_FLUCTUATION",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}发送[QPS]{currentValue}与历史同期值{expectedValue}波动较大。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 31
		},{
	    "metaId" : 31,
	    "type" : "CONSUMER_CONSUMERID_SENDMESSAGE_DELAY",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端发送延时告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}发送延时{currentValue}延时大于阈值{expectedValue}(s)。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 32
		},{
	    "metaId" : 32,
	    "type" : "CONSUMER_CONSUMERID_SENDMESSAGE_ACCUMULATION",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端消息累积告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}消息累积{currentValue}累积大于阈值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 33
		},{
	    "metaId" : 33,
	    "type" : "CONSUMER_CONSUMERID_ACKQPS_PEAK",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}确认[QPS]{currentValue}高于峰值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 34
		},{
	    "metaId" : 34,
	    "type" : "CONSUMER_CONSUMERID_ACKQPS_VALLEY",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}确认[QPS]{currentValue}低于谷值{expectedValue}。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 35
		},{
	    "metaId" : 35,
	    "type" : "CONSUMER_CONSUMERID_ACKQPS_FLUCTUATION",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}确认[QPS]{currentValue}与历史同期值{expectedValue}波动较大。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 36
		},{
	    "metaId" : 36,
	    "type" : "CONSUMER_CONSUMERID_ACKMESSAGE_DELAY",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端确认延时告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CONSUMERID]{consumerId}确认延时{currentValue}延时大于阈值{expectedValue}(s)。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 37
		},{
	    "metaId" : 37,
	    "type" : "CONSUMER_SERVER_SLAVESERVICE_STARTED",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器SLAVE服务开启告警",
	    "alarmTemplate" : "消费服务器[SLAVE IP]{ip}服务未开启。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 38
		},{
	    "metaId" : 38,
	    "type" : "PRODUCER_SERVER_PIGEON_SERVICE_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器服务告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}访问pigeon健康监测页面，已经正常。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 39
		},{
	    "metaId" : 39,
	    "type" : "PRODUCER_SERVER_SENDER_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器SENDER告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}发送统计数据到管理端，已正常。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 40
		},{
	    "metaId" : 40,
	    "type" : "CONSUMER_SERVER_SENDER_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器SENDER告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送统计数据到管理端，已正常。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		 "metaId" : 41
		},{
	    "metaId" : 41,
	    "type" : "CONSUMER_SERVER_PORT_OPENED_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器PORT告警",
	    "alarmTemplate" : "消费服务器[MASTER IP]{masterIp}[SLAVE IP]{slaveIp}，MASTER SLAVE端口已正常。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.swallowwebalarmmetac.update({
		"metaId" : 42
		},{
	    "metaId" : 42,
	    "type" : "CONSUMER_SERVER_SLAVESERVICE_STARTED_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器SLAVE服务开启告警",
	    "alarmTemplate" : "消费服务器[SLAVE IP]{ip}服务已正常开启。[{date}]",
	    "alarmDetail" : "",
	    "sendTimeSpan" : 5,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	print("swallowwebalarmmetac load data end");

}




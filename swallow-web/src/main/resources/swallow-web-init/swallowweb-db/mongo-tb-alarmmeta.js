db = db.getSiblingDB('swallowweb');
result = db.ALARM_META.findOne();

if(result != null) {
	print("ALARM_META already exist");
}else{

	print("ALARM_META load data start");
	
	db.ALARM_META.ensureIndex({'metaId': -1}, {"name":"IX_METAID", "background": true});
	
	db.ALARM_META.update({
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
	    "alarmTemplate" : "生产服务器[IP]{ip}不能访问pigeon健康监测页面，可能宕机。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 2
		},{
	    "metaId" : 2,
	    "type" : "PRODUCER_SERVER_PIGEON_SERVICE_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器服务告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}访问pigeon健康监测页面，已经正常。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 3
		}, {
	    "metaId" : 3,
	    "type" : "PRODUCER_SERVER_SENDER",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器SENDER告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}未发送统计数据到管理端，可能宕机。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 4
		},{
	    "metaId" : 4,
	    "type" : "PRODUCER_SERVER_SENDER_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器SENDER告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}发送统计数据到管理端，已正常。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 5
		}, {
	    "metaId" : 5,
	    "type" : "PRODUCER_SERVER_QPS_PEAK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器QPS告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}[QPS]{currentValue}高于峰值{expectedValue}。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 6
		}, {
	    "metaId" : 6,
	    "type" : "PRODUCER_SERVER_QPS_VALLEY",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器QPS告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}[QPS]{currentValue}低于谷值{expectedValue}。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 7
		}, {
	    "metaId" : 7,
	    "type" : "PRODUCER_SERVER_QPS_FLUCTUATION",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器QPS告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}[QPS]{currentValue}与历史同期值{expectedValue}波动较大。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 8
		}, {
	    "metaId" : 8,
	    "type" : "PRODUCER_SERVER_QPS_OK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "生产服务器QPS告警",
	    "alarmTemplate" : "生产服务器[IP]{ip}[QPS]正常。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 9
		}, {
	    "metaId" : 9,
	    "type" : "CONSUMER_SERVER_SENDER",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器SENDER告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}未发送统计数据到管理端，可能宕机。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 10
		},{
	    "metaId" : 10,
	    "type" : "CONSUMER_SERVER_SENDER_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器SENDER告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送统计数据到管理端，已正常。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
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
	    "alarmTemplate" : "消费服务器[MASTER IP]{masterIp}端口关闭状态，[SLAVE IP]{slaveIp}端口处于打开状态。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
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
	    "alarmTemplate" : "消费服务器[MASTER IP]{masterIp}[SLAVE IP]{slaveIp}端口都处于打开状态。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
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
	    "alarmTemplate" : "消费服务器[MASTER IP]{masterIp}[SLAVE IP]{slaveIp}端口都处于关闭状态。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	

	db.ALARM_META.update({
		 "metaId" : 14
		},{
	    "metaId" : 14,
	    "type" : "CONSUMER_SERVER_PORT_OPENED_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器PORT告警",
	    "alarmTemplate" : "消费服务器[MASTER IP]{masterIp}[SLAVE IP]{slaveIp}，端口已正常。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	

	db.ALARM_META.update({
		 "metaId" : 15
		},{
	    "metaId" : 15,
	    "type" : "CONSUMER_SERVER_SLAVESERVICE_STARTED",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器SLAVE服务告警",
	    "alarmTemplate" : "消费服务器[SLAVE IP]{ip}服务未开启。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		"metaId" : 16
		},{
	    "metaId" : 16,
	    "type" : "CONSUMER_SERVER_SLAVESERVICE_STARTED_OK",
	    "levelType" : "CRITICAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器SLAVE服务告警",
	    "alarmTemplate" : "消费服务器[SLAVE IP]{ip}服务已正常开启。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 17
		}, {
	    "metaId" : 17,
	    "type" : "CONSUMER_SERVER_SENDQPS_PEAK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器发送QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送[QPS]{currentValue}高于峰值{expectedValue}。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 18
		}, {
	    "metaId" : 18,
	    "type" : "CONSUMER_SERVER_SENDQPS_VALLEY",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器发送QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送[QPS]{currentValue}低于谷值{expectedValue}。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true,  false);
	
	db.ALARM_META.update({
		 "metaId" : 19
		}, {
	    "metaId" : 19,
	    "type" : "CONSUMER_SERVER_SENDQPS_FLUCTUATION",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器发送QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送[QPS]{currentValue}与历史同期值{expectedValue}波动较大。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 20
		}, {
	    "metaId" : 20,
	    "type" : "CONSUMER_SERVER_SENDQPS_OK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器发送QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}发送[QPS]已正常。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 21
		}, {
	    "metaId" : 21,
	    "type" : "CONSUMER_SERVER_ACKQPS_PEAK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器确认QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}确认[QPS]{currentValue}高于峰值{expectedValue}。",
	    "alarmDetail" : "",
	  	"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	},true,false);
	
	db.ALARM_META.update({
		 "metaId" : 22
		}, {
	    "metaId" : 22,
	    "type" : "CONSUMER_SERVER_ACKQPS_VALLEY",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器确认QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}确认[QPS]{currentValue}低于谷值{expectedValue}。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 23
		}, {
	    "metaId" : 23,
	    "type" : "CONSUMER_SERVER_ACKQPS_FLUCTUATION",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器确认QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}确认[QPS]{currentValue}与历史同期值{expectedValue}波动较大。",
	    "alarmDetail" : "",
	    "maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 24
		}, {
	    "metaId" : 24,
	    "type" : "CONSUMER_SERVER_ACKQPS_OK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "消费服务器确认QPS告警",
	    "alarmTemplate" : "消费服务器[IP]{ip}确认[QPS]已正常。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 25
		}, {
	    "metaId" : 25,
	    "type" : "SERVER_MONGO_CONFIG",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "服务器MONGO配置告警",
	    "alarmTemplate" : "服务器[IP]{ip}[TOPIC]{topic}配置与Lion配置不一致。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 26
		}, {
	    "metaId" : 26,
	    "type" : "SERVER_MONGO_CONFIG_OK",
	    "levelType" : "MAJOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : false,
	    "isSendSwallow" : true,
	    "isSendBusiness" : false,
	    "alarmTitle" : "服务器MONGO配置告警",
	    "alarmTemplate" : "服务器[IP]{ip}[TOPIC]{topic}配置已正常。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 27
	}, {
		"metaId" : 27,
		"type" : "SERVER_BROKER_STATE",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "kafka服务器告警",
		"alarmTemplate" : "kafka服务器[IP]{ip}宕机 {date}。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 28
	}, {
		"metaId" : 28,
		"type" : "SERVER_BROKER_STATE_OK",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "kafka服务器恢复告警",
		"alarmTemplate" : "kafka服务器[IP]{ip}已恢复 {date}。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 29
	}, {
		"metaId" : 29,
		"type" : "SERVER_CONTROLLER_STATE",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "kafka Controller告警",
		"alarmTemplate" : "kafka服务器[IP]{ip}未选举Controller {date}。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 30
	}, {
		"metaId" : 30,
		"type" : "SERVER_CONTROLLER_MULTI_STATE",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "kafka Controller告警",
		"alarmTemplate" : "kafka服务器选举多个Controller{ip} {date}。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 31
	}, {
		"metaId" : 31,
		"type" : "SERVER_CONTROLLER_STATE_OK",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "kafka Controller恢复告警",
		"alarmTemplate" : "kafka服务器[IP]{ip} Controller选举恢复正常 {date}。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 32
	}, {
		"metaId" : 32,
		"type" : "SERVER_CONTROLLER_ELECTION_STATE",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "kafka Controller告警",
		"alarmTemplate" : "kafka服务器[IP]{ip} Controller选举，由{expectedValue}切换到{currentValue}",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 33
	}, {
		"metaId" : 33,
		"type" : "SERVER_UNDERREPLICA_STATE",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "UnderReplicatedPartitions告警",
		"alarmTemplate" : "Kafka服务器[IP]{ip} [UnderReplicatedPartitions]{currentValue}高于阈值{expectedValue}。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 34
	}, {
		"metaId" : 34,
		"type" : "SERVER_UNDERREPLICA_STATE_OK",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "UnderReplicatedPartitions恢复告警",
		"alarmTemplate" : "Kafka服务器[IP]{ip} [UnderReplicatedPartitions]已恢复。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 35
	}, {
		"metaId" : 35,
		"type" : "SERVER_UNDERREPLICA_PARTITION_STATE",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "UnderReplicatedPartitions告警",
		"alarmTemplate" : "[Topic]{topic} [Partition]{consumerId} isr为{currentValue}, replica为{expectedValue}。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 36
	}, {
		"metaId" : 36,
		"type" : "SERVER_UNDERREPLICA_PARTITION_STATE_OK",
		"levelType" : "CRITICAL",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : true,
		"isSendBusiness" : false,
		"alarmTitle" : "UnderReplicatedPartitions恢复告警",
		"alarmTemplate" : "[Topic]{topic} [Partition]{consumerId} UnderReplicated已恢复。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		 "metaId" : 1001
		}, {
	    "metaId" : 1001,
	    "type" : "PRODUCER_TOPIC_QPS_PEAK",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产端TOPIC QPS告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}[QPS]{currentValue}高于峰值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1002
		}, {
	    "metaId" : 1002,
	    "type" : "PRODUCER_TOPIC_QPS_VALLEY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产端TOPIC QPS告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}[QPS]{currentValue}低于谷值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1003
		}, {
	    "metaId" : 1003,
	    "type" : "PRODUCER_TOPIC_QPS_FLUCTUATION",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产端TOPIC QPS告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}[QPS]{currentValue}与历史同期值{expectedValue}波动较大。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1004
		}, {
	    "metaId" : 1004,
	    "type" : "PRODUCER_TOPIC_MESSAGE_DELAY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产端TOPIC存储延时告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}存储延时{currentValue}大于阈值{expectedValue}(s)。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1005
		}, {
	    "metaId" : 1005,
	    "type" : "CONSUMER_TOPIC_SENDQPS_PEAK",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}发送[QPS]{currentValue}高于峰值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1006
		}, {
	    "metaId" : 1006,
	    "type" : "CONSUMER_TOPIC_SENDQPS_VALLEY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}发送[QPS]{currentValue}低于谷值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1007
		}, {
	    "metaId" : 1007,
	    "type" : "CONSUMER_TOPIC_SENDQPS_FLUCTUATION",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}发送[QPS]{currentValue}与历史同期值{expectedValue}波动较大。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1008
		}, {
	    "metaId" : 1008,
	    "type" : "CONSUMER_TOPIC_SENDMESSAGE_DELAY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC发送延时告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}发送延时{currentValue}延时大于阈值{expectedValue}(s)。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1009
		}, {
	    "metaId" : 1009,
	    "type" : "CONSUMER_TOPIC_ACKQPS_PEAK",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}确认[QPS]{currentValue}高于峰值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1010
		}, {
	    "metaId" : 1010,
	    "type" : "CONSUMER_TOPIC_ACKQPS_VALLEY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}确认[QPS]{currentValue}低于谷值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1011
		}, {
	    "metaId" : 1011,
	    "type" : "CONSUMER_TOPIC_ACKQPS_FLUCTUATION",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}确认[QPS]{currentValue}与历史同期值{expectedValue}波动较大。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1012
		},{
	    "metaId" : 1012,
	    "type" : "CONSUMER_TOPIC_ACKMESSAGE_DELAY",
	    "levelType" : "MINOR",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端TOPIC确认延时告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}确认延时{currentValue}延时大于阈值{expectedValue}(s)。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1013
		},{
	    "metaId" : 1013,
	    "type" : "CONSUMER_CONSUMERID_SENDQPS_PEAK",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}发送[QPS]{currentValue}高于峰值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1014
		},{
	    "metaId" : 1014,
	    "type" : "CONSUMER_CONSUMERID_SENDQPS_VALLEY",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}发送[QPS]{currentValue}低于谷值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1015
		},{
	    "metaId" : 1015,
	    "type" : "CONSUMER_CONSUMERID_SENDQPS_FLUCTUATION",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端发送QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}发送[QPS]{currentValue}与历史同期值{expectedValue}波动较大。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1016
		},{
	    "metaId" : 1016,
	    "type" : "CONSUMER_CONSUMERID_SENDMESSAGE_DELAY",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端发送延时告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}发送延时{currentValue}延时大于阈值{expectedValue}(s)。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1017
		},{
	    "metaId" : 1017,
	    "type" : "CONSUMER_CONSUMERID_SENDMESSAGE_ACCUMULATION",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端消息累积告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}消息累积{currentValue}累积大于阈值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1018
		},{
	    "metaId" : 1018,
	    "type" : "CONSUMER_CONSUMERID_ACKQPS_PEAK",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}确认[QPS]{currentValue}高于峰值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1019
		},{
	    "metaId" : 1019,
	    "type" : "CONSUMER_CONSUMERID_ACKQPS_VALLEY",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}确认[QPS]{currentValue}低于谷值{expectedValue}。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1020
		},{
	    "metaId" : 1020,
	    "type" : "CONSUMER_CONSUMERID_ACKQPS_FLUCTUATION",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端确认QPS告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}确认[QPS]{currentValue}与历史同期值{expectedValue}波动较大。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1021
		},{
	    "metaId" : 1021,
	    "type" : "CONSUMER_CONSUMERID_ACKMESSAGE_DELAY",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费端确认延时告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}确认延时{currentValue}延时大于阈值{expectedValue}(s)。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	
	db.ALARM_META.update({
		 "metaId" : 1022
		},{
	    "metaId" : 1022,
	    "type" : "PRODUCER_CLIENT_SENDER",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "生产客户端SENDER告警",
	    "alarmTemplate" : "生产客户端[TOPIC]{topic}[IP]{ip}{checkInterval}s内未生产消息，可能出现故障。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);
	
	db.ALARM_META.update({
		 "metaId" : 1023
		},{
	    "metaId" : 1023,
	    "type" : "CONSUMER_CLIENT_RECEIVER",
	    "levelType" : "GENERAL",
	    "isSmsMode" : true,
	    "isWeiXinMode" : true,
	    "isMailMode" : true,
	    "isSendSwallow" : false,
	    "isSendBusiness" : true,
	    "alarmTitle" : "消费客户端RECEIVER告警",
	    "alarmTemplate" : "消费客户端[TOPIC]{topic}[CID]{consumerId}[IP]{ip}{checkInterval}s内未消费消息，可能出现故障。",
	    "alarmDetail" : "",
  		"maxTimeSpan" : 120,
	    "daySpanBase" : 10,
	    "nightSpanBase" : 20,
	    "createTime" : new Date(),
	    "updateTime" : new Date()
	}, true, false);

	db.ALARM_META.update({
		"metaId" : 1024
	}, {
		"metaId" : 1024,
		"type" : "PRODUCER_TOPIC_MESSAGE_SIZE",
		"levelType" : "MINOR",
		"isSmsMode" : true,
		"isWeiXinMode" : true,
		"isMailMode" : true,
		"isSendSwallow" : false,
		"isSendBusiness" : true,
		"alarmTitle" : "生产端TOPIC消息大小告警",
		"alarmTemplate" : "生产客户端[TOPIC]{topic}消息大小{currentValue}大于阈值{expectedValue}(kb)。",
		"alarmDetail" : "",
		"maxTimeSpan" : 120,
		"daySpanBase" : 10,
		"nightSpanBase" : 20,
		"createTime" : new Date(),
		"updateTime" : new Date()
	}, true, false);

	print("ALARM_META load data end");

}




db = db.getSiblingDB('swallowweb');

//PRODUCER_SERVER_RESOURCE
print("PRODUCER_SERVER_ALARM_SETTING export to PRODUCER_SERVER_RESOURCE start");

var srcData = db.PRODUCER_SERVER_ALARM_SETTING.find();
destData = db.PRODUCER_SERVER_RESOURCE.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		var resource = {};
		resource.ip = "default";
		resource.hostname = "default";
		resource.alarm = true;
		resource.saveAlarmSetting = temp.alarmSetting;
		resource.createTime = new Date();
		resource.updateTime = new Date();
		db.PRODUCER_SERVER_RESOURCE.insert(resource);
		break;
	}
} else {
	print("PRODUCER_SERVER_ALARM_SETTING no data or PRODUCER_SERVER_RESOURCE already exsits");
}
print("PRODUCER_SERVER_ALARM_SETTING export to PRODUCER_SERVER_RESOURCE end");


//CONSUMER_SERVER_RESOURCE
print("CONSUMER_SERVER_ALARM_SETTING export to CONSUMER_SERVER_RESOURCE start");

var srcData = db.CONSUMER_SERVER_ALARM_SETTING.find();
destData = db.CONSUMER_SERVER_RESOURCE.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		var resource = {};
		resource.ip = "default";
		resource.hostname = "default";
		resource.alarm = true;
		resource.sendAlarmSetting = temp.sendAlarmSetting;
		resource.ackAlarmSetting = temp.ackAlarmSetting;
		resource.createTime = new Date();
		resource.updateTime = new Date();
		db.CONSUMER_SERVER_RESOURCE.insert(resource);
		break;
	}
} else {
	print("CONSUMER_SERVER_ALARM_SETTING no data or CONSUMER_SERVER_RESOURCE already exsits");
}
print("CONSUMER_SERVER_ALARM_SETTING export to CONSUMER_SERVER_RESOURCE end");


//TOPIC_RESOURCE
print("TOPIC_ALARM_SETTING export to TOPIC_RESOURCE start");

var srcData = db.TOPIC_ALARM_SETTING.find();
destData = db.TOPIC_RESOURCE.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		var resource = {};
		resource.topic = "default";
		resource.administrator = "default";
		resource.producerAlarm = true;
		resource.consumerAlarm = true;
		resource.producerIps = [];
		resource.producerAlarmSetting = temp.producerAlarmSetting;
		resource.createTime = new Date();
		resource.updateTime = new Date();
		db.TOPIC_RESOURCE.insert(resource);
		break;
	}
} else {
	print("TOPIC_ALARM_SETTING no data or TOPIC_RESOURCE already exsits");
}
print("TOPIC_ALARM_SETTING export to TOPIC_RESOURCE end");


//CONSUMERID_RESOURCE
print("CONSUMERID_ALARM_SETTING export to CONSUMERID_RESOURCE start");

var srcData = db.CONSUMERID_ALARM_SETTING.find();
destData = db.CONSUMERID_RESOURCE.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		var resource = {};
		resource.consumerId = "default";
		resource.topic = "default";
		resource.alarm = true;
		resource.consumerIps = [];
		resource.consumerAlarmSetting = temp.consumerAlarmSetting;
		resource.createTime = new Date();
		resource.updateTime = new Date();
		db.CONSUMERID_RESOURCE.insert(resource);
		break;
	}
} else {
	print("CONSUMERID_ALARM_SETTING no data or CONSUMERID_RESOURCE already exsits");
}
print("CONSUMERID_ALARM_SETTING export to CONSUMERID_RESOURCE end");


//IP_RESOURCE
print("IP_DESC export to IP_RESOURCE start");

var srcData = db.IP_DESC.find();
destData = db.IP_RESOURCE.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		var resource = {};
		resource.ip = temp.ip;
		resource.alarm = false;
		resource.iPDesc = temp;
		resource.createTime = new Date();
		resource.updateTime = new Date();
		db.IP_RESOURCE.insert(resource);
	}
} else {
	print("IP_DESC no data or IP_RESOURCE already exsits");
}
print("IP_DESC export to IP_RESOURCE end");





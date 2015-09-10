db = db.getSiblingDB('swallowweb');

//PRODUCER_SERVER_RESOURCE
print("PRODUCER_SERVER_RESOURCE load data start");

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
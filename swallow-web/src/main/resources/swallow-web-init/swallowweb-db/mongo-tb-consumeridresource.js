db = db.getSiblingDB('swallowweb');

// CONSUMERID_RESOURCE
print("CONSUMERID_RESOURCE load data start");

data = db.CONSUMERID_RESOURCE.findOne();
if (data == null) {

	db.CONSUMERID_RESOURCE.ensureIndex({
		'topic' : -1,
		'consumerId' : -1
	}, {
		"name" : "IX_TOPIC_CONSUMERID",
		"unique" : true,
		"dropDups" : true,
		"background" : true
	});

	var resource = {};
	resource.consumerId = "default";
	resource.topic = "default";
	resource.alarm = true;
	resource.consumerIps = [];
	resource.consumerAlarmSetting = {
		"sendQpsAlarmSetting" : {
			"peak" : NumberLong(1000),
			"valley" : NumberLong(0),
			"fluctuation" : 10,
			"fluctuationBase" : NumberLong(100)
		},
		"ackQpsAlarmSetting" : {
			"peak" : NumberLong(1000),
			"valley" : NumberLong(0),
			"fluctuation" : 10,
			"fluctuationBase" : NumberLong(100)
		},
		"sendDelay" : NumberLong(300),
		"ackDelay" : NumberLong(300),
		"accumulation" : NumberLong(100000)
	};
	resource.createTime = new Date();
	resource.updateTime = new Date();
	db.CONSUMERID_RESOURCE.insert(resource);
} else {
	print("CONSUMERID_RESOURCE no data or CONSUMERID_RESOURCE already exsits");
}
print("CONSUMERID_RESOURCE load data end");
db = db.getSiblingDB('swallowstatsdata');

// ALARM
print("ALARM load data start");

data = db.ALARM.findOne();
if (data == null) {

	db.createCollection("ALARM", {
		capped : true,
		size : 52428800
	});

	db.ALARM.ensureIndex({
		'eventId' : -1
	}, {
		"name" : "IX_EVENTID",
		"background" : true
	});
	db.ALARM.ensureIndex({
		'createTime' : -1
	}, {
		"name" : "IX_CREATETIME",
		"background" : true
	});
	db.ALARM.ensureIndex({
		'related' : 1
	}, {
		"name" : "IX_RELATED",
		"background" : true
	});
	db.ALARM.ensureIndex({
		'sendInfos.receiver' : 1
	}, {
		"name" : "IX_SENDINFOS_RECEIVER",
		"background" : true
	});

} else {
	print("ALARM already exsits");
}
print("ALARM load data end");
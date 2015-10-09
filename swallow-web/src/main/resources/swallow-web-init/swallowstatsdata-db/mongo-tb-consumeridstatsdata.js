db = db.getSiblingDB('swallowstatsdata');

// CONSUMERID_STATS_DATA
print("CONSUMERID_STATS_DATA load data start");

data = db.CONSUMERID_STATS_DATA.findOne();
if (data == null) {

	db.createCollection("CONSUMERID_STATS_DATA", {
		capped : true,
		size : 52428800
	});

	db.CONSUMERID_STATS_DATA.ensureIndex({
		'timeKey' : 1
	}, {
		"name" : "IX_TIMEKEY",
		"background" : true
	});
	db.CONSUMERID_STATS_DATA.ensureIndex({
		'topicName' : -1,
		'consumerId' : -1,
		'timeKey' : 1
	}, {
		"name" : "IX_TOPICNAME_CONSUMERID_TIMEKEY",
		'background' : true
	});

} else {
	print("CONSUMERID_STATS_DATA already exsits");
}
print("CONSUMERID_STATS_DATA load data end");
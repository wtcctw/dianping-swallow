db = db.getSiblingDB('swallowstatsdata');

// PRODUCER_IP_STATS_DATA
print("PRODUCER_IP_STATS_DATA load data start");

data = db.PRODUCER_IP_STATS_DATA.findOne();
if (data == null) {

	db.createCollection("PRODUCER_IP_STATS_DATA", {
		capped : true,
		size : 52428800
	});

	db.PRODUCER_IP_STATS_DATA.ensureIndex({
		'timeKey' : 1
	}, {
		"name" : "IX_TIMEKEY",
		"background" : true
	});
	db.PRODUCER_IP_STATS_DATA.ensureIndex({
		'topicName' : -1,
		'ip' : -1,
		'timeKey' : 1
	}, {
		"name" : "IX_TOPICNAME_IP_TIMEKEY",
		'background' : true
	});

} else {
	print("PRODUCER_IP_STATS_DATA already exsits");
}
print("PRODUCER_IP_STATS_DATA load data end");

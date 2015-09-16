db = db.getSiblingDB('swallowstatsdata');

// CONSUMER_IP_STATS_DATA
print("CONSUMER_IP_STATS_DATA load data start");

data = db.CONSUMER_IP_STATS_DATA.findOne();
if (data == null) {

	db.createCollection("CONSUMER_IP_STATS_DATA", {
		capped : true,
		size : 52428800
	});

	db.CONSUMER_IP_STATS_DATA.ensureIndex({
		'timeKey' : 1
	}, {
		"name" : "IX_TIMEKEY",
		"background" : true
	});
	db.CONSUMER_IP_STATS_DATA.ensureIndex({
		'topicName' : -1,
		'consumerId' : -1,
		'ip' : -1,
		'timeKey' : 1
	}, {
		"name" : "IX_TOPICNAME_CONSUMERID_IP_TIMEKEY",
		'background' : true
	});

} else {
	print("CONSUMER_IP_STATS_DATA already exsits");
}
print("CONSUMER_IP_STATS_DATA load data end");
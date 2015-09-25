db = db.getSiblingDB('swallowstatsdata');

// PRODUCER_TOPIC_STATS_DATA
print("PRODUCER_TOPIC_STATS_DATA load data start");

data = db.PRODUCER_TOPIC_STATS_DATA.findOne();
if (data == null) {

	db.createCollection("PRODUCER_TOPIC_STATS_DATA", {
		capped : true,
		size : 52428800
	});

	db.PRODUCER_TOPIC_STATS_DATA.ensureIndex({
		'timeKey' : 1
	}, {
		"name" : "IX_TIMEKEY",
		"background" : true
	});
	db.PRODUCER_TOPIC_STATS_DATA.ensureIndex({
		'topicName' : -1,
		'timeKey' : 1
	}, {
		"name" : "IX_TOPICNAME_TIMEKEY",
		"background" : true
	});

} else {
	print("PRODUCER_TOPIC_STATS_DATA already exsits");
}
print("PRODUCER_TOPIC_STATS_DATA load data end");

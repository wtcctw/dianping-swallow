db = db.getSiblingDB('swallowstatsdata');

// MONGO_STATS_DATA
print("MONGO_STATS_DATA load data start");

data = db.MONGO_STATS_DATA.findOne();
if (data == null) {

	db.createCollection("MONGO_STATS_DATA", {
		capped : true,
		size : 52428800
	});

	db.MONGO_STATS_DATA.ensureIndex({
		'timeKey' : 1
	}, {
		"name" : "IX_TIMEKEY",
		"background" : true
	});

	db.MONGO_STATS_DATA.ensureIndex({
		'ips' : 1,
		'timeKey' : 1
	}, {
		"name" : "IX_IPS_TIMEKEY",
		'background' : true
	});

} else {
	print("MONGO_STATS_DATA already exsits");
}
print("MONGO_STATS_DATA load data end");
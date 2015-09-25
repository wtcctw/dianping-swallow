db = db.getSiblingDB('swallowstatsdata');

// DASHBOARD_STATS_DATA
print("DASHBOARD_STATS_DATA load data start");

data = db.DASHBOARD_STATS_DATA.findOne();
if (data == null) {

	db.createCollection("DASHBOARD_STATS_DATA", {
		capped : true,
		size : 52428800
	});

	db.DASHBOARD_STATS_DATA.ensureIndex({
		'time' : 1
	}, {
		"name" : "IX_TIME",
		"background" : true
	});

} else {
	print("DASHBOARD_STATS_DATA already exsits");
}
print("DASHBOARD_STATS_DATA load data end");
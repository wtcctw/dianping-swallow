//swallowwebapplication ====> swallowweb
srcDb = db.getSiblingDB('swallowwebapplication');

destDb = db.getSiblingDB('swallowweb');

print("swallowwebapplication export to swallowweb")

// ALARM_META
print("swallowwebalarmmetac export to ALARM_META start");
var srcData = srcDb.swallowwebalarmmetac.find();
destData = destDb.ALARM_META.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.ALARM_META.insert(temp);
	}
} else {
	print("swallowwebalarmmetac no data or ALARM_META already exsits");
}
print("swallowwebalarmmetac export to ALARM_META end");

// ADMIN
print("swallowwebadminc export to ADMIN start");
var srcData = srcDb.swallowwebadminc.find();
destData = destDb.ADMIN.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		temp.date = new Date(Date.parse(temp.date.replace(/-/g, "/")));
		destDb.ADMIN.insert(temp);
	}
} else {
	print("swallowwebadminc no data or ADMIN already exsits");
}
print("swallowwebadminc export to ADMIN end");

// GLOBAL_ALARM_SETTING
print("swallowwebswallowalarmsettingc export to GLOBAL_ALARM_SETTING start");
var srcData = srcDb.swallowwebswallowalarmsettingc.find();
destData = destDb.GLOBAL_ALARM_SETTING.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.GLOBAL_ALARM_SETTING.insert(temp);
	}
} else {
	print("swallowwebswallowalarmsettingc no data or GLOBAL_ALARM_SETTING already exsits");
}
print("swallowwebswallowalarmsettingc export to GLOBAL_ALARM_SETTING end");

// CONSUMERID_ALARM_SETTING
print("swallowwebconsumeridalarmsettingc export to CONSUMERID_ALARM_SETTING start");
var srcData = srcDb.swallowwebconsumeridalarmsettingc.find();
destData = destDb.CONSUMERID_ALARM_SETTING.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.CONSUMERID_ALARM_SETTING.insert(temp);
	}
} else {
	print("swallowwebconsumeridalarmsettingc no data or CONSUMERID_ALARM_SETTING already exsits");
}
print("swallowwebconsumeridalarmsettingc export to CONSUMERID_ALARM_SETTING end");

// CONSUMER_SERVER_ALARM_SETTING
print("swallowwebconsumerserveralarmsettingc export to CONSUMER_SERVER_ALARM_SETTING start");
var srcData = srcDb.swallowwebconsumerserveralarmsettingc.find();
destData = destDb.CONSUMER_SERVER_ALARM_SETTING.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.CONSUMER_SERVER_ALARM_SETTING.insert(temp);
	}
} else {
	print("swallowwebconsumerserveralarmsettingc no data or CONSUMER_SERVER_ALARM_SETTING already exsits");
}
print("swallowwebconsumerserveralarmsettingc export to CONSUMER_SERVER_ALARM_SETTING end");

// PRODUCER_SERVER_ALARM_SETTING
print("swallowwebproducerserveralarmsettingc export to PRODUCER_SERVER_ALARM_SETTING start");
var srcData = srcDb.swallowwebproducerserveralarmsettingc.find();
destData = destDb.PRODUCER_SERVER_ALARM_SETTING.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.PRODUCER_SERVER_ALARM_SETTING.insert(temp);
	}
} else {
	print("swallowwebproducerserveralarmsettingc no data or PRODUCER_SERVER_ALARM_SETTING already exsits");
}
print("swallowwebproducerserveralarmsettingc export to PRODUCER_SERVER_ALARM_SETTING end");

// TOPIC_ALARM_SETTING
print("swallowwebtopicalarmsettingc export to TOPIC_ALARM_SETTING start");
var srcData = srcDb.swallowwebtopicalarmsettingc.find();
destData = destDb.TOPIC_ALARM_SETTING.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.TOPIC_ALARM_SETTING.insert(temp);
	}
} else {
	print("swallowwebtopicalarmsettingc no data or TOPIC_ALARM_SETTING already exsits");
}
print("swallowwebtopicalarmsettingc export to TOPIC_ALARM_SETTING end");

// TOPIC
print("swallowwebtopicc export to TOPIC start");
var srcData = srcDb.swallowwebtopicc.find();
destData = destDb.TOPIC.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		temp.time = new Date(Date.parse(temp.time.replace(/-/g, "/")));
		destDb.TOPIC.insert(temp);
	}
} else {
	print("swallowwebtopicc no data or TOPIC already exsits");
}
print("swallowwebtopicc export to TOPIC end");

// SEQ_GENERATOR
print("swallowwebseqgeneratorc export to SEQ_GENERATOR start");
var srcData = srcDb.swallowwebseqgeneratorc.find();
destData = destDb.SEQ_GENERATOR.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.SEQ_GENERATOR.insert(temp);
	}
} else {
	print("swallowwebseqgeneratorc no data or SEQ_GENERATOR already exsits");
}
print("swallowwebseqgeneratorc export to SEQ_GENERATOR end");

// MESSAGE_DUMP
print("swallowwebmessagedumpc export to MESSAGE_DUMP start");
var srcData = srcDb.swallowwebmessagedumpc.find();
destData = destDb.MESSAGE_DUMP.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		temp.startdt = new Date(Date.parse(temp.startdt.replace(/-/g, "/")));
		temp.stopdt = new Date(Date.parse(temp.stopdt.replace(/-/g, "/")));
		temp.time = new Date(Date.parse(temp.time.replace(/-/g, "/")));
		destDb.MESSAGE_DUMP.insert(temp);
	}
} else {
	print("swallowwebmessagedumpc no data or MESSAGE_DUMP already exsits");
}
print("swallowwebmessagedumpc export to MESSAGE_DUMP end");

// IP_DESC
print("swallowwebipdescc export to IP_DESC start");
var srcData = srcDb.swallowwebipdescc.find();
destData = destDb.IP_DESC.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.IP_DESC.insert(temp);
	}
} else {
	print("swallowwebipdescc no data or IP_DESC already exsits");
}
print("swallowwebipdescc export to IP_DESC end");

// swallowwebapplication ====> swallowstatsdata

srcDb = db.getSiblingDB('swallowwebapplication');

destDb = db.getSiblingDB('swallowstatsdata');
// ALARM
print("swallowwebalarmdatac export to ALARM start");
var srcData = srcDb.swallowwebalarmdatac.find();
destData = destDb.ALARM.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.ALARM.insert(temp);
	}
} else {
	print("swallowwebalarmdatac no data or ALARM already exsits");
}
print("swallowwebalarmdatac export to ALARM end");

// swallowwebapplication ====> swallowstatsdata

srcDb = db.getSiblingDB('swallowalarmstats');

destDb = db.getSiblingDB('swallowstatsdata');

// PRODUCER_SERVER_STATS_DATA
print("ProducerServerStatsData export to PRODUCER_SERVER_STATS_DATA start");
var srcData = srcDb.ProducerServerStatsData.find();
destData = destDb.PRODUCER_SERVER_STATS_DATA.findOne();
if (srcData != null && destData == null) {
	 db.createCollection('PRODUCER_SERVER_STATS_DATA', {'capped' : true, 'size' : 1073741824, 'max' :12960000 }); 
	 db.PRODUCER_SERVER_STATS_DATA.ensureIndex({'timeKey': 1, 'ip': -1},{"name":"IX_TIMEKEY_IP"});
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.PRODUCER_SERVER_STATS_DATA.insert(temp);
	}
} else {
	print("ProducerServerStatsData no data or PRODUCER_SERVER_STATS_DATA already exsits");
}
print("ProducerServerStatsData export to PRODUCER_SERVER_STATS_DATA end");

// PRODUCER_TOPIC_STATS_DATA
print("ProducerTopicStatsData export to PRODUCER_TOPIC_STATS_DATA start");
var srcData = srcDb.ProducerTopicStatsData.find();
destData = destDb.PRODUCER_TOPIC_STATS_DATA.findOne();
if (srcData != null && destData == null) {
	 db.createCollection('PRODUCER_SERVER_STATS_DATA', {'capped' : true, 'size' : 214748364800, 'max' :5000000000 }); 
	 db.PRODUCER_SERVER_STATS_DATA.ensureIndex({'timeKey': 1, 'topicName': -1},{"name":"IX_TIMEKEY_TOPICNAME"});
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.PRODUCER_TOPIC_STATS_DATA.insert(temp);
	}
} else {
	print("ProducerTopicStatsData no data or PRODUCER_TOPIC_STATS_DATA already exsits");
}
print("ProducerTopicStatsData export to PRODUCER_TOPIC_STATS_DATA end");

// CONSUMER_SERVER_STATS_DATA
print("ConsumerServerStatsData export to CONSUMER_SERVER_STATS_DATA start");
var srcData = srcDb.ConsumerServerStatsData.find();
destData = destDb.CONSUMER_SERVER_STATS_DATA.findOne();
if (srcData != null && destData == null) {
	 db.createCollection('PRODUCER_SERVER_STATS_DATA', {'capped' : true, 'size' : 1073741824, 'max' :12960000 }); 
	 db.PRODUCER_SERVER_STATS_DATA.ensureIndex({'timeKey': 1, 'ip': -1},{"name":"IX_TIMEKEY_IP"});
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.CONSUMER_SERVER_STATS_DATA.insert(temp);
	}
} else {
	print("ConsumerServerStatsData no data or CONSUMER_SERVER_STATS_DATA already exsits");
}
print("ConsumerServerStatsData export to CONSUMER_SERVER_STATS_DATA end");

// CONSUMERID_STATS_DATA
print("ConsumerIdStatsData export to CONSUMERID_STATS_DATA start");
var srcData = srcDb.ConsumerIdStatsData.find();
destData = destDb.CONSUMERID_STATS_DATA.findOne();
if (srcData != null && destData == null) {
	 db.createCollection('CONSUMERID_STATS_DATA', {'capped' : true, 'size' : 322122547200, 'max' :10000000000 }); 
	 db.CONSUMERID_STATS_DATA.ensureIndex({'timeKey': 1, 'topicName': -1, 'consumerId': -1}, {"name":"IX_TIMEKEY_TOPICNAME_CONSUMERID"});
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.CONSUMERID_STATS_DATA.insert(temp);
	}
} else {
	print("ConsumerIdStatsData no data or CONSUMERID_STATS_DATA already exsits");
}
print("ConsumerIdStatsData export to CONSUMERID_STATS_DATA end");


// DashboardStatsData
print("swallowwebdashboardc export to DashboardStatsData start");
var srcData = srcDb.swallowwebdashboardc.find();
destData = destDb.DashboardStatsData.findOne();
if (srcData != null && destData == null) {
	while (srcData.hasNext()) {
		var temp = srcData.next();
		destDb.DashboardStatsData.insert(temp);
	}
} else {
	print("swallowwebdashboardc no data or DashboardStatsData already exsits");
}
print("swallowwebdashboardc export to DashboardStatsData end");

print("swallowwebapplication export to swallowweb end");

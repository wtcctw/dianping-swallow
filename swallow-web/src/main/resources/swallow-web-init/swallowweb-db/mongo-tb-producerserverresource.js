db = db.getSiblingDB('swallowweb');

// PRODUCER_SERVER_RESOURCE
print("PRODUCER_SERVER_RESOURCE load data start");

data = db.PRODUCER_SERVER_RESOURCE.findOne();
if (data == null) {
	
	db.PRODUCER_SERVER_RESOURCE.ensureIndex({'ip': -1 }, {"name":"IX_IP", "unique": true, "dropDups" : true, "background": true});
	
	var resource = {};
	resource.ip = "default";
	resource.hostname = "default";
	resource.alarm = true;
	resource.saveAlarmSetting =  {
	       "peak" : NumberLong(1500), 
	       "valley" : NumberLong(0), 
	       "fluctuation" : 10,
	       "fluctuationBase" : NumberLong(100)
	     };
	resource.createTime = new Date();
	resource.updateTime = new Date();
	db.PRODUCER_SERVER_RESOURCE.insert(resource);
} else {
	print("PRODUCER_SERVER_RESOURCE already exsits");
}

print("PRODUCER_SERVER_RESOURCE load data end");
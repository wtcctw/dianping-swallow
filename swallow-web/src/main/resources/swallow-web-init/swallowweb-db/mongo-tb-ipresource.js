db = db.getSiblingDB('swallowweb');
// IP_RESOURCE
print("IP_RESOURCE load data start");

data = db.IP_RESOURCE.findOne();
if (data == null) {
	
	db.IP_RESOURCE.ensureIndex({'application': -1, 'ip': -1}, {"name":"IX_APPLICATION_IP", "unique": true, "dropDups" : true, "background": true});
	db.IP_RESOURCE.ensureIndex({'ip': -1}, {"name":"IX_IP", "unique": true, "dropDups" : true, "background": true});
	
} else {
	print("IP_RESOURCE already exsits");
}
print("IP_RESOURCE load data end");
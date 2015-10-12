

db = db.getSiblingDB('swallowweb');
// APPLICATION_RESOURCE
print("APPLICATION_RESOURCE load data start");

data = db.APPLICATION_RESOURCE.findOne();
if (data == null) {
	
	db.APPLICATION_RESOURCE.ensureIndex({'application': 1 }, {"name":"IX_APPLICATION", "unique": true, "dropDups" : true, "background": true});
	
} else {
	print("APPLICATION_RESOURCE already exsits");
}
print("APPLICATION_RESOURCE load data end");
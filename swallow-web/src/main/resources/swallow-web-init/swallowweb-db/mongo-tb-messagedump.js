db = db.getSiblingDB('swallowweb');
// MESSAGE_DUMP
print("MESSAGE_DUMP load data start");

data = db.MESSAGE_DUMP.findOne();
if (data == null) {
	
	db.MESSAGE_DUMP.ensureIndex({'topic': -1 }, {"name":"IX_TOPIC", "background": true});
	
} else {
	print("MESSAGE_DUMP already exsits");
}
print("MESSAGE_DUMP load data end");
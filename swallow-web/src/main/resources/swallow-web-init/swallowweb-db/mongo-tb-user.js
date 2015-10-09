db = db.getSiblingDB('swallowweb');
// USER
print("USER load data start");

data = db.USER.findOne();
if (data == null) {
	
	db.USER.ensureIndex({'name': 1 }, {"name":"IX_NAME", "background": true});
	
} else {
	print("USER already exsits");
}
print("USER load data end");
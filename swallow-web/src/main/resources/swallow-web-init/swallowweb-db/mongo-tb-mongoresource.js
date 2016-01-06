/**
 * Created by mingdongli on 16/1/6.
 */

db = db.getSiblingDB('swallowweb');

// MONGO_RESOURCE
print("MONGO_RESOURCE load data start");

data = db.MONGO_RESOURCE.findOne();
if (data == null) {

    db.MONGO_RESOURCE.ensureIndex({'catalog': -1}, {"name":"IX_CATALOG", "background": true});
    db.MONGO_RESOURCE.ensureIndex({'ip': -1 }, {"name":"IX_IP", "unique": true, "dropDups" : true, "background": true});

} else {
    print("MONGO_RESOURCE already exsits");
}
print("MONGO_RESOURCE load data end");
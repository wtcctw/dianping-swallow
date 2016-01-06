/**
 * Created by mingdongli on 16/1/6.
 */

db = db.getSiblingDB('swallowweb');

// GROUP_RESOURCE
print("GROUP_RESOURCE load data start");

data = db.GROUP_RESOURCE.findOne();
if (data == null) {

    db.GROUP_RESOURCE.ensureIndex({'groupName': 1 }, {"name":"IX_GROUP", "unique": true, "dropDups" : true, "background": true});

} else {
    print("GROUP_RESOURCE already exsits");
}
print("GROUP_RESOURCE load data end");
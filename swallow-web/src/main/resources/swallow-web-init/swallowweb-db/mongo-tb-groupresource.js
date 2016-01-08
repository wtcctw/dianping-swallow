/**
 * Created by mingdongli on 16/1/6.
 */

db = db.getSiblingDB('swallowweb');

// GROUP_RESOURCE
print("GROUP_RESOURCE load data start");

data = db.GROUP_RESOURCE.findOne();
if (data == null) {

    db.GROUP_RESOURCE.ensureIndex({'groupName': 1 }, {"name":"IX_GROUP", "unique": true, "dropDups" : true, "background": true});

    var resource1 = {};
    resource1.groupName = "default";
    resource1.desc = "默认消息队列";

    var resource2 = {};
    resource2.groupName = "pay";
    resource2.desc = "下单消息队列";

    var resource3 = {};
    resource3.groupName = "search";
    resource3.desc = "搜索消息队列";

    var resource4 = {};
    resource4.groupName = "kafka-default";
    resource4.desc = "kafka默认消息队列";

    db.GROUP_RESOURCE.insert([
        resource1,
        resource2,
        resource3,
        resource4
    ]);

} else {
    print("GROUP_RESOURCE already exsits");
}
print("GROUP_RESOURCE load data end");
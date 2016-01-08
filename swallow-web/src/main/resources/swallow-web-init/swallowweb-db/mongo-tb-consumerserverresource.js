db = db.getSiblingDB('swallowweb');

// CONSUMER_SERVER_RESOURCE
print("CONSUMER_SERVER_RESOURCE load data start");

data = db.CONSUMER_SERVER_RESOURCE.findOne();
if (data == null) {

    db.CONSUMER_SERVER_RESOURCE.ensureIndex({'ip': -1}, {
        "name": "IX_IP",
        "unique": true,
        "dropDups": true,
        "background": true
    });

    var resource = {};
    resource.ip = "default";
    resource.hostname = "default";
    resource.alarm = true;
    resource.isQpsAlarm = true;
    resource.sendAlarmSetting = {
        "peak": NumberLong(15000),
        "valley": NumberLong(1),
        "fluctuation": 20,
        "fluctuationBase": NumberLong(100)
    };
    resource.ackAlarmSetting = {
        "peak": NumberLong(15000),
        "valley": NumberLong(1),
        "fluctuation": 20,
        "fluctuationBase": NumberLong(100)
    };
    resource.createTime = new Date();
    resource.updateTime = new Date();
    db.CONSUMER_SERVER_RESOURCE.insert(resource);
} else {
    print("CONSUMER_SERVER_RESOURCE already exsits");
}
print("CONSUMER_SERVER_RESOURCE load data end");
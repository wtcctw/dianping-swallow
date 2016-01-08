db = db.getSiblingDB('swallowweb');
// TOPIC_RESOURCE
print("TOPIC_RESOURCE load data start");

data = db.TOPIC_RESOURCE.findOne();
if (data == null) {

    db.TOPIC_RESOURCE.ensureIndex({
        'topic': 1
    }, {
        "name": "IX_TOPIC",
        "unique": true,
        "dropDups": true,
        "background": true
    });

    var resource = {};
    resource.topic = "default";
    resource.administrator = "default";
    resource.producerAlarm = true;
    resource.consumerAlarm = true;
    resource.producerIps = [];
    resource.producerAlarmSetting = {
        "isQpsAlarm": true,
        "qpsAlarmSetting": {
            "peak": NumberLong(1000),
            "valley": NumberLong(0),
            "fluctuation": 10,
            "fluctuationBase": NumberLong(200)
        },
        "isDelayAlarm": true,
        "delay": NumberLong(300),
        "isIpAlarm": true,
    };
    resource.createTime = new Date();
    resource.updateTime = new Date();
    db.TOPIC_RESOURCE.insert(resource);
} else {
    print("TOPIC_RESOURCE already exsits");
}
print("TOPIC_RESOURCE load data end");
db = db.getSiblingDB('swallowweb');

//PRODUCER_SERVER_RESOURCE etl
print("PRODUCER_SERVER_RESOURCE etl data start.");

var data = db.PRODUCER_SERVER_RESOURCE.find();

if (data != null) {
    while (data.hasNext()) {
        var temp = data.next();
        temp.isQpsAlarm = true;
        db.PRODUCER_SERVER_RESOURCE.save(temp);
    }
} else {
    print("PRODUCER_SERVER_RESOURCE no data.");
}

print("PRODUCER_SERVER_RESOURCE etl data end.");


//CONSUMER_SERVER_RESOURCE etl
print("CONSUMER_SERVER_RESOURCE etl data start.");

var data = db.CONSUMER_SERVER_RESOURCE.find();

if (data != null) {
    while (data.hasNext()) {
        var temp = data.next();
        temp.isQpsAlarm = true;
        db.CONSUMER_SERVER_RESOURCE.save(temp);
    }
} else {
    print("CONSUMER_SERVER_RESOURCE no data.");
}

print("CONSUMER_SERVER_RESOURCE etl data end.");


//TOPIC_RESOURCE etl
print("TOPIC_RESOURCE etl data start.");

var data = db.TOPIC_RESOURCE.find();

if (data != null) {
    while (data.hasNext()) {
        var temp = data.next();
        temp.producerAlarmSetting.isQpsAlarm = true;
        temp.producerAlarmSetting.isDelayAlarm = true;
        temp.producerAlarmSetting.isIpAlarm = true;
        db.TOPIC_RESOURCE.save(temp);
    }
} else {
    print("TOPIC_RESOURCE no data.");
}

print("TOPIC_RESOURCE etl data end.");


//CONSUMERID_RESOURCE etl
print("CONSUMERID_RESOURCE etl data start.");

var data = db.CONSUMERID_RESOURCE.find();

if (data != null) {
    while (data.hasNext()) {
        var temp = data.next();
        temp.consumerAlarmSetting.isQpsAlarm = true;
        temp.consumerAlarmSetting.isDelayAlarm = true;
        temp.consumerAlarmSetting.isAccuAlarm = true;
        temp.consumerAlarmSetting.isIpAlarm = true;
        db.CONSUMERID_RESOURCE.save(temp);
    }
} else {
    print("CONSUMERID_RESOURCE no data.");
}

print("CONSUMERID_RESOURCE etl data end.");
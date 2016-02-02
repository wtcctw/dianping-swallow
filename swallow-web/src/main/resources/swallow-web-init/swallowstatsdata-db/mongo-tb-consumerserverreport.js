db = db.getSiblingDB('swallowstatsdata');

// CONSUMER_SERVER_REPORT
print("CONSUMER_SERVER_REPORT load data start");

data = db.CONSUMER_SERVER_REPORT.findOne();
if (data == null) {

    db.CONSUMER_SERVER_REPORT.ensureIndex({
        'timeKey' : -1
    }, {
        "name" : "IX_TIMEKEY",
        "background" : true
    });

    db.CONSUMER_SERVER_REPORT.ensureIndex({
        'ip' : -1,
        'timeKey' : -1
    }, {
        "name" : "IX_IP_TIMEKEY",
        'background' : true
    });

} else {
    print("CONSUMER_SERVER_REPORT already exsits");
}
print("CONSUMER_SERVER_REPORT load data end");
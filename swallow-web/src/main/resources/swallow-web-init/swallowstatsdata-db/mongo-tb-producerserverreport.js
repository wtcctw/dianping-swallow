db = db.getSiblingDB('swallowstatsdata');

// PRODUCER_SERVER_REPORT
print("PRODUCER_SERVER_REPORT load data start");

data = db.PRODUCER_SERVER_REPORT.findOne();
if (data == null) {

    db.PRODUCER_SERVER_REPORT.ensureIndex({
        'timeKey' : -1
    }, {
        "name" : "IX_TIMEKEY",
        "background" : true
    });

    db.PRODUCER_SERVER_REPORT.ensureIndex({
        'ip' : -1,
        'timeKey' : -1
    }, {
        "name" : "IX_IP_TIMEKEY",
        'background' : true
    });

} else {
    print("PRODUCER_SERVER_REPORT already exsits");
}
print("PRODUCER_SERVER_REPORT load data end");
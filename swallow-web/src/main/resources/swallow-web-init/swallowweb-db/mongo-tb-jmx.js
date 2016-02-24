/**
 * Created by mingdongli on 16/2/19.
 */
db = db.getSiblingDB('swallowweb');
// JMX_RESOURCE
print("JMX_RESOURCE load data start");

data = db.JMX_RESOURCE.findOne();
if (data == null) {

    db.JMX_RESOURCE.ensureIndex({'name': 1}, {"name":"IX_NAME", "unique": true, "dropDups" : true, "background": true});

    var resource1 = {};
    resource1.group = "kafka.server";
    resource1.name = "BrokerState";
    resource1.type = "KafkaServer";
    resource1.tag = "";
    resource1.clazz = "Gauge";

    var resource2 = {};
    resource2.group = "kafka.server";
    resource2.name = "UnderReplicatedPartitions";
    resource2.type = "ReplicaManager";
    resource2.tag = "";
    resource2.clazz = "Gauge";

    db.JMX_RESOURCE.insert([
        resource1,
        resource2
    ]);

} else {
    print("JMX_RESOURCE already exsits");
}
print("JMX_RESOURCE load data end");

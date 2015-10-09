
//跟新发送时间间隔
db.ALARM_META.update({ "sendTimeSpan" : 5 }, { $set: {"sendTimeSpan" : 6 } }, false, true);

//是否发送swallow相关人员
db.ALARM_META.update({ "isSendSwallow" : false}, { $set: {"isSendSwallow" : true } }, false, true);

//是否发送业务人员
db.ALARM_META.update({ "isSendBusiness" : true}, { $set: {"isSendBusiness" : false } }, false, true);

//是否发送邮件
db.ALARM_META.update({ "isMailMode" : false}, { $set: {"isMailMode" : true } }, false, true);

//仅修改server报警
db.ALARM_META.update({ "isMailMode" : false, "metaId" : {$gte : 1 ,  $lt: 1000}}, { $set: {"isMailMode" : true } }, false, true);

//仅修改业务相关
db.ALARM_META.update({ "isMailMode" : false, "metaId" : {$gt : 1000 }}, { $set: {"isMailMode" : true } }, false, true);


db.ALARM_META.update({ "isSendBusiness" : true }, { $set: {"isSendBusiness" : false } }, false, true);

db.ALARM_META.update({ "isSendSwallow" : true }, { $set: {"isSendSwallow" : false } }, false, true);

db.ALARM_META.update({ "isSendSwallow" : false, "metaId" : {$gte : 1 ,  $lt: 1000}}, { $set: {"isSendSwallow" : true } }, false, true);



db.ALARM_META.update({ "isSmsMode" : true }, { $set: {"isSmsMode" : false } }, false, true);


db.ALARM_META.update({ "isMailMode" : false }, { $set: {"isMailMode" : true } }, false, true);



db.runCommand({"convertToCapped": "CONSUMERID_STATS_DATA", size: 52428800, max:1000000});

db.runCommand({"convertToCapped": "ALARM", size: 52428800 });
db.runCommand({"convertToCapped": "DASHBOARD_STATS_DATA", size: 52428800 });
db.runCommand({"convertToCapped": "PRODUCER_SERVER_STATS_DATA", size: 52428800 });
db.runCommand({"convertToCapped": "PRODUCER_TOPIC_STATS_DATA", size: 52428800 });
db.runCommand({"convertToCapped": "CONSUMER_SERVER_STATS_DATA", size: 52428800 });
db.runCommand({"convertToCapped": "CONSUMER_TOPIC_STATS_DATA", size: 52428800 });
db.runCommand({"convertToCapped": "CONSUMERID_STATS_DATA", size: 52428800 });


db.ALARM.ensureIndex({'eventId': -1}, {"name":"IX_EVENTID", "background": true});
db.ALARM.ensureIndex({'createTime': -1}, {"name":"IX_CREATETIME", "background": true});
db.ALARM.ensureIndex({'related': 1}, {"name":"IX_RELATED", "background": true});
db.ALARM.ensureIndex({'sendInfos.receiver': 1}, {"name":"IX_SENDINFOS_RECEIVER", "background": true});

db.DASHBOARD_STATS_DATA.ensureIndex({'time': 1}, {"name":"IX_TIME", "background": true});

db.PRODUCER_SERVER_STATS_DATA.ensureIndex({'timeKey': 1}, {"name":"IX_TIMEKEY", "background": true});
db.PRODUCER_SERVER_STATS_DATA.ensureIndex({'ip': -1 ,'timeKey': 1 }, {"name":"IX_IP_TIMEKEY", "background": true});

db.PRODUCER_TOPIC_STATS_DATA.ensureIndex({'timeKey': 1}, {"name":"IX_TIMEKEY", "background": true});
db.PRODUCER_TOPIC_STATS_DATA.ensureIndex({'topicName': -1, 'timeKey': 1 }, {"name":"IX_TOPICNAME_TIMEKEY", "background": true});

db.CONSUMER_SERVER_STATS_DATA.ensureIndex({'timeKey': 1}, {"name":"IX_TIMEKEY", "background": true});
db.CONSUMER_SERVER_STATS_DATA.ensureIndex({'ip': -1, 'timeKey': 1}, {"name":"IX_IP_TIMEKEY", "background": true});

db.CONSUMER_TOPIC_STATS_DATA.ensureIndex({'timeKey': 1}, {"name":"IX_TIMEKEY", "background": true});
db.CONSUMER_TOPIC_STATS_DATA.ensureIndex({'topicName': -1, 'timeKey': 1}, {"name":"IX_TOPICNAME_TIMEKEY", "background": true});

db.CONSUMERID_STATS_DATA.ensureIndex({'timeKey': 1}, {"name":"IX_TIMEKEY", "background": true});
db.CONSUMERID_STATS_DATA.ensureIndex({ 'topicName': -1, 'consumerId': -1,'timeKey': 1 }, {"name":"IX_TOPICNAME_CONSUMERID_TIMEKEY", 'background': true});

db.PRODUCER_TOPIC_STATS_DATA.ensureIndex({ 'topicName': -1, 'timeKey': 1 }, {"name":"IX_TOPICNAME_TIMEKEY", "background": true});





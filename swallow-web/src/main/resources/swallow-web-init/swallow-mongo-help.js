
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

db.ALARM_META.update({ "isWeiXinMode" : true }, { $set: {"isWeiXinMode" : false } }, false, true);

db.ALARM_META.update({ "isMailMode" : false }, { $set: {"isMailMode" : true } }, false, true);



db.runCommand({"convertToCapped": "CONSUMERID_STATS_DATA", size: 52428800, max:1000000});
<<<<<<< Updated upstream
=======



>>>>>>> Stashed changes

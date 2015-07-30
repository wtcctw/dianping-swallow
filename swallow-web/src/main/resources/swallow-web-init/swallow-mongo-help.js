
//跟新发送时间间隔
db.swallowwebalarmmetac.update({ "sendTimeSpan" : 5 }, { $set: {"sendTimeSpan" : 6 } }, false, true);

//是否发送swallow相关人员
db.swallowwebalarmmetac.update({ "isSendSwallow" : false}, { $set: {"isSendSwallow" : true } }, false, true);

//是否发送业务人员
db.swallowwebalarmmetac.update({ "isSendBusiness" : true}, { $set: {"isSendBusiness" : false } }, false, true);

//是否发送邮件
db.swallowwebalarmmetac.update({ "isMailMode" : false}, { $set: {"isMailMode" : true } }, false, true);

//仅修改server报警
db.swallowwebalarmmetac.update({ "isMailMode" : false, "metaId" : {$gte : 1 ,  $lt: 1000}}, { $set: {"isMailMode" : true } }, false, true);

//仅修改业务相关
db.swallowwebalarmmetac.update({ "isMailMode" : false, "metaId" : {$gt : 1000 }}, { $set: {"isMailMode" : true } }, false, true);


db.swallowwebalarmmetac.update({ "isSendBusiness" : true}, { $set: {"isSendBusiness" : false } }, false, true);

db.swallowwebalarmmetac.update({ "isSendSwallow" : true}, { $set: {"isSendSwallow" : false } }, false, true);

db.swallowwebalarmmetac.update({ "isSendSwallow" : false, "metaId" : {$gte : 1 ,  $lt: 1000}}, { $set: {"isSendSwallow" : true } }, false, true);



db.swallowwebalarmmetac.update({ "isSmsMode" : true}, { $set: {"isSmsMode" : false } }, false, true);

db.swallowwebalarmmetac.update({ "isWeiXinMode" : true}, { $set: {"isWeiXinMode" : false } }, false, true);

db.swallowwebalarmmetac.update({ "isMailMode" : false}, { $set: {"isMailMode" : true } }, false, true);


//跟新某个字段
db.swallowwebalarmmetac.update({ "sendTimeSpan" : 5}, { $set: {"sendTimeSpan" : 6}}, false, true);

db.swallowwebalarmmetac.update({ "isSendSwallow" : false}, { $set: {"isSendSwallow" : true}}, false, true);

db.swallowwebalarmmetac.update({ "isSendBusiness" : true}, { $set: {"isSendBusiness" : false}}, false, true);


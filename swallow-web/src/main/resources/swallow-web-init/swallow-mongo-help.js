
//跟新发送时间间隔
db.swallowwebalarmmetac.update({ "sendTimeSpan" : 5 }, { $set: {"sendTimeSpan" : 6 }}, false, true);

//是否发送swallow相关人员
db.swallowwebalarmmetac.update({ "isSendSwallow" : false}, { $set: {"isSendSwallow" : true}}, false, true);

//是否发送业务人员
db.swallowwebalarmmetac.update({ "isSendBusiness" : true}, { $set: {"isSendBusiness" : false}}, false, true);

//是否发送邮件
db.swallowwebalarmmetac.update({ "isMailMode" : false}, { $set: {"isMailMode" : true}}, false, true);



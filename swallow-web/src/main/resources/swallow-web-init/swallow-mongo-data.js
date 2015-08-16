//swallowwebapplication ====> swallowweb
srcDb = db.getSiblingDB('swallowwebapplication');

destDb = db.getSiblingDB('swallowweb');

print("swallowwebapplication export to swallowweb")

// AlarmMeta 
print("swallowwebalarmmetac export to AlarmMeta start");
var srcData = srcDb.swallowwebalarmmetac.find();
destData = destDb.AlarmMeta.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.AlarmMeta.insert(temp);
	}
}else{
	print("swallowwebalarmmetac no data or AlarmMeta already exsits");
}
print("swallowwebalarmmetac export to AlarmMeta end");

//Admin
print("swallowwebadminc export to Admin start");
var srcData = srcDb.swallowwebadminc.find();
destData = destDb.Admin.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.Admin.insert(temp);
	}
}else{
	print("swallowwebadminc no data or Admin already exsits");
}
print("swallowwebadminc export to Admin end");

//GlobalAlarmSetting
print("swallowwebswallowalarmsettingc export to GlobalAlarmSetting start");
var srcData = srcDb.swallowwebswallowalarmsettingc.find();
destData = destDb.GlobalAlarmSetting.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.GlobalAlarmSetting.insert(temp);
	}
}else{
	print("swallowwebswallowalarmsettingc no data or GlobalAlarmSetting already exsits");
}
print("swallowwebswallowalarmsettingc export to GlobalAlarmSetting end");

//ConsumerIdAlarmSetting
print("swallowwebconsumeridalarmsettingc export to ConsumerIdAlarmSetting start");
var srcData = srcDb.swallowwebconsumeridalarmsettingc.find();
destData = destDb.ConsumerIdAlarmSetting.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.ConsumerIdAlarmSetting.insert(temp);
	}
}else{
	print("swallowwebconsumeridalarmsettingc no data or ConsumerIdAlarmSetting already exsits");
}
print("swallowwebconsumeridalarmsettingc export to ConsumerIdAlarmSetting end");


//ConsumerServerAlarmSetting
print("swallowwebconsumerserveralarmsettingc export to ConsumerServerAlarmSetting start");
var srcData = srcDb.swallowwebconsumerserveralarmsettingc.find();
destData = destDb.ConsumerServerAlarmSetting.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.ConsumerServerAlarmSetting.insert(temp);
	}
}else{
	print("swallowwebconsumerserveralarmsettingc no data or ConsumerServerAlarmSetting already exsits");
}
print("swallowwebconsumerserveralarmsettingc export to ConsumerServerAlarmSetting end");

//ProducerServerAlarmSetting
print("swallowwebproducerserveralarmsettingc export to ProducerServerAlarmSetting start");
var srcData = srcDb.swallowwebproducerserveralarmsettingc.find();
destData = destDb.ProducerServerAlarmSetting.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.ProducerServerAlarmSetting.insert(temp);
	}
}else{
	print("swallowwebproducerserveralarmsettingc no data or ProducerServerAlarmSetting already exsits");
}
print("swallowwebproducerserveralarmsettingc export to ProducerServerAlarmSetting end");

//TopicAlarmSetting
print("swallowwebtopicalarmsettingc export to TopicAlarmSetting start");
var srcData = srcDb.swallowwebtopicalarmsettingc.find();
destData = destDb.TopicAlarmSetting.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.TopicAlarmSetting.insert(temp);
	}
}else{
	print("swallowwebtopicalarmsettingc no data or TopicAlarmSetting already exsits");
}
print("swallowwebtopicalarmsettingc export to TopicAlarmSetting end");

//Topic
print("swallowwebtopicc export to Topic start");
var srcData = srcDb.swallowwebtopicc.find();
destData = destDb.Topic.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.Topic.insert(temp);
	}
}else{
	print("swallowwebtopicc no data or Topic already exsits");
}
print("swallowwebtopicc export to Topic end");

//SeqGenerator
print("swallowwebseqgeneratorc export to Topic start");
var srcData = srcDb.swallowwebseqgeneratorc.find();
destData = destDb.SeqGenerator.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.SeqGenerator.insert(temp);
	}
}else{
	print("swallowwebseqgeneratorc no data or SeqGenerator already exsits");
}
print("swallowwebseqgeneratorc export to SeqGenerator end");

//MessageDump
print("swallowwebmessagedumpc export to Topic start");
var srcData = srcDb.swallowwebmessagedumpc.find();
destData = destDb.MessageDump.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.MessageDump.insert(temp);
	}
}else{
	print("swallowwebmessagedumpc no data or MessageDump already exsits");
}
print("swallowwebmessagedumpc export to MessageDump end");

//swallowwebapplication ====> swallowweb


//IPDesc
print("swallowwebipdescc export to Topic start");
var srcData = srcDb.swallowwebipdescc.find();
destData = destDb.IPDesc.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.IPDesc.insert(temp);
	}
}else{
	print("swallowwebipdescc no data or IPDesc already exsits");
}
print("swallowwebipdescc export to IPDesc end");

//alarm
print("swallowwebalarmdatac export to Alarm start");
var srcData = srcDb.swallowwebalarmdatac.find();
destData = destDb.Alarm.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.Alarm.insert(temp);
	}
}else{
	print("swallowwebalarmdatac no data or Alarm already exsits");
}
print("swallowwebalarmdatac export to Alarm end");

//DashboardStatsData
print("swallowwebdashboardc export to DashboardStatsData start");
var srcData = srcDb.swallowwebdashboardc.find();
destData = destDb.DashboardStatsData.findOne();
if (srcData != null && destData == null) {
        while(srcData.hasNext()){
		var temp = srcData.next();
		destDb.DashboardStatsData.insert(temp);
	}
}else{
	print("swallowwebdashboardc no data or DashboardStatsData already exsits");
}
print("swallowwebdashboardc export to DashboardStatsData end");



print("swallowwebapplication export to swallowweb end");





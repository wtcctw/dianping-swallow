db = db.getSiblingDB('swallowweb');

result = db.CONSUMERID_ALARM_SETTING.findOne();
if(result == null) {

	print("CONSUMERID_ALARM_SETTING load data start");
	
	db.CONSUMERID_ALARM_SETTING.update({
	    "consumerId" : "default"
	    },{
	    "consumerId" : "default",
	    "topicName" : "default",
	    "consumerAlarmSetting" : {
	        "sendQpsAlarmSetting" : { 
	            "peak" : NumberLong(40), 
	            "valley" : NumberLong(1), 
	            "fluctuation" : 3,
	            "fluctuationBase" : NumberLong(30)
	         }, 
	        "ackQpsAlarmSetting" : { 
	            "peak" : NumberLong(40), 
	            "valley" : NumberLong(1), 
	            "fluctuation" : 3,
	            "fluctuationBase" : NumberLong(30)
	         }, 
	         "sendDelay" : NumberLong(3), 
	         "ackDelay" : NumberLong(500), 
	         "accumulation" : NumberLong(1) 
	     }
	}, true, false);
	
	print("CONSUMERID_ALARM_SETTING load data end");
	
}else{
	print("CONSUMERID_ALARM_SETTING already exist");
}

result = db.CONSUMER_SERVER_ALARM_SETTING.findOne();
if(result == null) {
	
	print("CONSUMER_SERVER_ALARM_SETTING load data start");
	
	db.CONSUMER_SERVER_ALARM_SETTING.update({
	    "serverId" : "default"
	    },{
	    "serverId" : "default",
	    "topicWhiteList" : [ "x" ], 
	    "sendAlarmSetting" : { 
	        "peak" : NumberLong(1), 
	        "valley" : NumberLong(1), 
	        "fluctuation" : 20,
	        "fluctuationBase" : NumberLong(20)
	    }, 
	    "ackAlarmSetting" : { 
	        "peak" : NumberLong(1), 
	        "valley" : NumberLong(1), 
	        "fluctuation" : 20,
	        "fluctuationBase" : NumberLong(20)
	    }
	}, true, false);
	
	print("CONSUMER_SERVER_ALARM_SETTING load data end");
}else{
	
	print("CONSUMER_SERVER_ALARM_SETTING already exist");
	
}


result = db.PRODUCER_SERVER_ALARM_SETTING.findOne();
if(result == null) {
	
	print("PRODUCER_SERVER_ALARM_SETTING load data start");
	
	db.PRODUCER_SERVER_ALARM_SETTING.update({
	    "serverId" : "default"
	    },{
	    "serverId" : "default", 
	    "topicWhiteList" : [ "x" ], 
	    "alarmSetting" : {
	       "peak" : NumberLong(100), 
	       "valley" : NumberLong(30), 
	       "fluctuation" : 10,
	       "fluctuationBase" : NumberLong(10)
	     }
	}, true, false);
	
	print("PRODUCER_SERVER_ALARM_SETTING load data start");
	
}else{
	
	print("PRODUCER_SERVER_ALARM_SETTING already exist");
	
}


result = db.GLOBAL_ALARM_SETTING.findOne();
if(result == null) {
	
	print("GLOBAL_ALARM_SETTING load data start");
	
	db.GLOBAL_ALARM_SETTING.update({
	    "swallowId" : "default"
	    },{
	    "swallowId" : "default", 
	    "producerWhiteList" : [ "10.128.121.229" ], 
	    "consumerWhiteList" : [ "10.128.121.229" ] 
	}, true, false);

	print("GLOBAL_ALARM_SETTING load data end");
	
}else{
	
	print("GLOBAL_ALARM_SETTING already exist");
	
}

result = db.TOPIC_ALARM_SETTING.findOne();
if(result == null) {
	
	print("TOPIC_ALARM_SETTING load data start");
	
	db.TOPIC_ALARM_SETTING.update({
	    "topicName" : "default"
	    }, {
	    "topicName" : "default", 
	    "consumerIdWhiteList" : [ "x" ], 
	    "producerAlarmSetting" : {
	        "qpsAlarmSetting" : { 
	            "peak" : NumberLong(100), 
	            "valley" : NumberLong(1),
	            "fluctuation" : 2,
	            "fluctuationBase" : NumberLong(10)
	        }, 
	        "delay" : NumberLong(1) 
	    }, 
	    "consumerAlarmSetting" : { 
	        "sendQpsAlarmSetting" : { 
	            "peak" : NumberLong(30),
	            "valley" : NumberLong(5),
	            "fluctuation" : 5,
	            "fluctuationBase" : NumberLong(10)
	        }, 
	        "ackQpsAlarmSetting" : { 
	            "peak" : NumberLong(30), 
	            "valley" : NumberLong(5), 
	            "fluctuation" : 5,
	            "fluctuationBase" : NumberLong(10)
	        }, 
	        "sendDelay" : NumberLong(30), 
	        "ackDelay" : NumberLong(5), 
	        "accumulation" : NumberLong(10)
	    }
	}, true, false);
	
	print("TOPIC_ALARM_SETTING load data start");
	
}else{
	
	print("TOPIC_ALARM_SETTING already exist");
	
}


db = db.getSiblingDB('swallowwebapplication');

result = db.swallowwebconsumeridalarmsettingc.findOne();
if(result == null) {

	print("swallowwebconsumeridalarmsettingc load data start");
	
	db.swallowwebconsumeridalarmsettingc.update({
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
	
	print("swallowwebconsumeridalarmsettingc load data end");
	
}else{
	print("swallowwebconsumeridalarmsettingc already exist");
}

result = db.swallowwebconsumerserveralarmsettingc.findOne();
if(result == null) {
	
	print("swallowwebconsumerserveralarmsettingc load data start");
	
	db.swallowwebconsumerserveralarmsettingc.update({
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
	
	print("swallowwebconsumerserveralarmsettingc load data end");
}else{
	
	print("swallowwebconsumerserveralarmsettingc already exist");
	
}


result = db.swallowwebproducerserveralarmsettingc.findOne();
if(result == null) {
	
	print("swallowwebproducerserveralarmsettingc load data start");
	
	db.swallowwebproducerserveralarmsettingc.update({
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
	
	print("swallowwebproducerserveralarmsettingc load data start");
	
}else{
	
	print("swallowwebproducerserveralarmsettingc already exist");
	
}


result = db.swallowwebswallowalarmsettingc.findOne();
if(result == null) {
	
	print("swallowwebswallowalarmsettingc load data start");
	
	db.swallowwebswallowalarmsettingc.update({
	    "swallowId" : "default"
	    },{
	    "swallowId" : "default", 
	    "producerWhiteList" : [ "10.128.121.229" ], 
	    "consumerWhiteList" : [ "10.128.121.229" ] 
	}, true, false);

	print("swallowwebswallowalarmsettingc load data end");
	
}else{
	
	print("swallowwebswallowalarmsettingc already exist");
	
}

result = db.swallowwebtopicalarmsettingc.findOne();
if(result == null) {
	
	print("swallowwebtopicalarmsettingc load data start");
	
	db.swallowwebtopicalarmsettingc.update({
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
	
	print("swallowwebtopicalarmsettingc load data start");
	
}else{
	
	print("swallowwebtopicalarmsettingc already exist");
	
}


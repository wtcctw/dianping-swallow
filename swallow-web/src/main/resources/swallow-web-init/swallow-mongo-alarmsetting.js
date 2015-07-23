db = db.getSiblingDB('swallowwebapplication');

result = db.swallowwebconsumeridalarmsettingc.findOne();
if(result == null) {
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
}

result = db.swallowwebconsumerserveralarmsettingc.findOne();
if(result == null) {
	db.swallowwebconsumerserveralarmsettingc.update({
	    "serverId" : "defalut"
	    },{
	    "serverId" : "default",
	    "topicWhiteList" : [ "x" ], 
	    "senderAlarmSetting" : { 
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
}


result = db.swallowwebproducerserveralarmsettingc.findOne();
if(result == null) {
	db.swallowwebproducerserveralarmsettingc.update({
	    "serverId" : "default"
	    },{
	    "serverId" : "default", 
	    "topicWhiteList" : [ "x" ], 
	    "defaultAlarmSetting" : {
	       "peak" : NumberLong(100), 
	       "valley" : NumberLong(30), 
	       "fluctuation" : 10,
	       "fluctuationBase" : NumberLong(10)
	     }
	}, true, false);
}


result = db.swallowwebswallowalarmsettingc.findOne();
if(result == null) {
	db.swallowwebswallowalarmsettingc.update({
	    "swallowId" : "default"
	    },{
	    "swallowId" : "default", 
	    "producerWhiteList" : [ "10.128.121.229" ], 
	    "consumerWhiteList" : [ "10.128.121.229" ] 
	}, true, false);

}

result = db.swallowwebtopicalarmsettingc.findOne();
if(result == null) {
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
}


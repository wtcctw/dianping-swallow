package com.dianping.swallow.test.btrace;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.sun.btrace.AnyType;
import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.Return;

import static com.sun.btrace.BTraceUtils.*;

/**
 * @author mengwenchao
 *
 * 2015.6.3 4:27:08
 */

@BTrace
public class GetTopics {

    @OnMethod(
            clazz="com.dianping.swallow.web.task.TopicScanner",
            method="getTopics",
            location=@Location(Kind.RETURN)
        )
        public static void getTopics(@Return Map<String, Set<String>> topics) {
    	
            println(Strings.str(topics));
        }

		@OnMethod(
            clazz="com.dianping.swallow.web.monitor.impl.DefaultAccumulationRetriever",
            method="buildAllAccumulations"
        )
        public static void buildAllAccumulationsBegin(AnyType[] args) {
    		println("=========================begin");
        }
    	
    	@OnMethod(
                clazz="com.dianping.swallow.web.monitor.impl.DefaultAccumulationRetriever",
                method="putAccumulation"
            )
            public static void putAccumulation(AnyType[] args) throws IOException {
    			log("putAccumulation begin");
        		log(args);
    			log("putAccumulation begin");
            }
    	

    	@OnMethod(
                clazz="com.dianping.swallow.web.monitor.impl.DefaultAccumulationRetriever",
                method="buildAllAccumulations",
                location=@Location(Kind.RETURN)
    			)
    	public static void buildAllAccumulationsend(AnyType[] args) {
        		println("=========================end ");
    	}

    	
    @OnMethod(
            clazz="com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl",
            method="getAccumulation"
        )
        public static void addConsumerId(AnyType[] args) throws IOException {
    	
    		log(args);
        }

    
    
    @OnMethod(
            clazz="com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl",
            method="getAccumulation",
            location=@Location(Kind.RETURN)
        )
        public static void addConsumerIdRet(@Return long result) {
    	
    		println(result);
        }

	private static void log(String info) throws IOException {

		Appendable appender = getBaseInfo();
		Strings.append(appender, info);
		print(appender);
	}
	
	private static Appendable getBaseInfo() {
		
		String threadName = name(currentThread());
		Appendable appendable = Strings.newStringBuilder();
		Strings.append(appendable, threadName);
		return Strings.append(appendable, ",");
	}

	private static void log(AnyType[] args) throws IOException {
		
		print(getBaseInfo());
		printArray(args);

	}


}

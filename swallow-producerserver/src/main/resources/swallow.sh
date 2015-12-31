#!/bin/bash
PRGDIR=`dirname "$0"`
ACTION=$1
ProcessName="ProducerServerBootstrap"

if [ ! -d "/data/applogs/swallow" ] ; then
  mkdir -p "/data/applogs/swallow"
fi

##################################################
# Some utility functions
##################################################
running()
{
    PID=$1
    ps -p $PID >/dev/null 2>/dev/null || return 1
    return 0
}
usage(){
    echo "  Usage:"
    echo "     use '$0 start/restart/stop'."
    exit 1
}
mysleep(){
    TIMEOUT=$1
    while [ $TIMEOUT -gt 0 ]
      do
        sleep 1
        let TIMEOUT=$TIMEOUT-1
        echo -en "\aTime left $TIMEOUT sec.\r"
    done
    echo ""
}

######### 关闭进程 #########
if [ "$ACTION" = "stop" -o "$ACTION" = "restart" ];  then
    echo "========================= swallow stoping =========================="
    PidCount=$(jps |grep $ProcessName|wc -l)
    if [ $PidCount -ge 1 ]; then
        Pid=$(jps |grep $ProcessName |cut -d\  -f1)
        echo "Now stoping $ProcessName(pid=$Pid) ..."
        #模仿jetty的方式去kill
        TIMEOUT=8
        while running $Pid && [ $TIMEOUT -gt 0 ]
          do
            kill $Pid 2>/dev/null
            sleep 1
            let TIMEOUT=$TIMEOUT-1
            echo -en "\aStoping, please wait for $TIMEOUT sec ...\r"
          done
        [ $TIMEOUT -gt 0 ] || kill -9 $Pid 2>/dev/null
        echo ""
        #确保进程已经关闭
        PidCount=$(jps |grep $ProcessName|wc -l)
        while [ $PidCount -ge 1 ]
         do
           Pid=$(jps |grep $ProcessName |cut -d\  -f1)
           echo  "Pid $Pid is still running! Now Kill it ..."
           kill -9  $Pid
           PidCount=$(jps |grep $ProcessName|wc -l)
        done
       echo "Pid $Pid is stoped."
    else
       echo "No process of name '$ProcessName' is running, so no need to stop. "
    fi
    if [ "$ACTION" == "stop" ] ; then
       exit 1
    fi
fi

######### 启动进程 #########
#确保进程已经关闭
PidCount=$(jps |grep $ProcessName|wc -l)
if [ $PidCount -ge 1 ]; then
    Pid=$(jps |grep $ProcessName |cut -d\  -f1)
    echo  "$ProcessName with Pid $Pid is already running! Can not start ..."
    exit 1
fi
echo "========================= swallow starting =========================="
if [ ! -d "/data/applogs/swallow" ] ; then
  mkdir -p "/data/applogs/swallow"
fi

JAVA_OPTS="-cp ${PRGDIR}/.:${PRGDIR}/* -server -Xms512m -Xmx2g -XX:+HeapDumpOnOutOfMemoryError -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9013 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:/data/applogs/swallow/swallow-producerserver-gc.log"
MAIN_CLASS="com.dianping.swallow.producerserver.bootstrap.ProducerServerBootstrap"

STD_OUT="/data/applogs/swallow/swallow-producerserver-std.out"
echo "starting..."
echo "output: $STD_OUT"
exec java $JAVA_OPTS $MAIN_CLASS > "$STD_OUT" 2>&1 &

###########  检查是否启动成功 ############
SuccessLog="Producer service for client is ready"
echo "Sleeping 5 sec for waiting process started ..."
mysleep 5

LogFile="/data/applogs/swallow/swallow-producerserver-std.out"
CheckResult=$(grep "$SuccessLog" $LogFile |wc -l)

if [ $CheckResult -ge 1 ]
        then
        LogView=$(grep "$SuccessLog" $LogFile)
        echo -e "Started \033[32mSucessfully\033[0m."
        echo "View of Log: $LogView"
else
        echo "Started \033[31mError\033[0m."
        tail -n 10 $LogFile
fi

Pid=$(jps |grep $ProcessName |cut -d\  -f1)
echo "$ProcessName started as PID $Pid."

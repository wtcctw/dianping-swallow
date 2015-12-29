#!/bin/bash
PRGDIR=`dirname "$0"`
ACTION=$1
MODE=$2
MASTER_IP=$3

. ${PRGDIR}/common.sh

LOCAL_IP=`ifconfig  | grep 'inet addr:' | cut -d: -f2 | awk 'BEGIN{count=0}$1!~/127.0.0.1/{if(count == 0){print $1;count++}}'`




##################################################
# Some utility functions
##################################################
usage(){
    echo "  Usage:"
    echo "     use '$0 start/restart/stop master' as master."
    echo "     use '$0 start/restart/stop slave [<masterIp>]' as slave."
    exit 1
}


if [ "$MODE" == "master" ]; then
    ProcessName="MasterBootStrap"
elif [ "$MODE" == "slave" ]; then
    ProcessName="SlaveBootStrap"
else
    log "Your input is not corrent!"
    usage
    exit 1
fi

######### 关闭进程 #########
if [ "$ACTION" = "stop" -o "$ACTION" = "restart" ];  then
    log "========================= swallow stoping =========================="
    PidCount=$(jps |grep $ProcessName|wc -l)
    if [ $PidCount -ge 1 ]; then
        Pid=$(jps |grep $ProcessName |cut -d\  -f1)
        log "Now stoping $ProcessName(pid=$Pid) ..."
        #模仿jetty的方式去kill
        TIMEOUT=5
        while running $Pid && [ $TIMEOUT -gt 0 ]
          do
            kill $Pid 2>/dev/null
            sleep 1
            let TIMEOUT=$TIMEOUT-1
            log  "Stoping, please wait for $TIMEOUT sec ...\r"
          done
        [ $TIMEOUT -gt 0 ] || kill -9 $Pid 2>/dev/null
        log ""
        #确保进程已经关闭
        PidCount=$(jps |grep $ProcessName|wc -l)
        while [ $PidCount -ge 1 ]
         do
           Pid=$(jps |grep $ProcessName |cut -d\  -f1)
           log  "Pid $Pid is still running! Now Kill it ..."
           kill -9  $Pid
           PidCount=$(jps |grep $ProcessName|wc -l)
        done
       log "Pid $Pid is stoped."
    else
       log "No process of name '$ProcessName' is running, so no need to stop. "
    fi
    if [ "$ACTION" == "stop" ] ; then
       exit 0
    fi
fi

######### 启动进程 #########
#确保进程已经关闭
PidCount=$(jps |grep $ProcessName|wc -l)
if [ $PidCount -ge 1 ]; then
    Pid=$(jps |grep $ProcessName |cut -d\  -f1)
    log  "$ProcessName with Pid $Pid is already running! Can not start ..."
    exit 1
fi

log "========================= swallow starting =========================="
MASTER_JMX_PORT=9011
SLAVE_JMX_PORT=9012
MEM=`getmem`
CLASSPATH=${PRGDIR}/../lib/swallow/*:${PRGDIR}/../lib/dianping/*:${PRGDIR}/../lib/others/*:${PRGDIR}/../conf/:${PRGDIR}/../conf/consumer/
JAVA_OPTS="${JAVA_OPTS}  -DforceDistrubuted=true -cp ${CLASSPATH} -server -XX:NewRatio=1 -Xms${MEM} -Xmx${MEM} -XX:+HeapDumpOnOutOfMemoryError -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"
MASTER_JAVA_OPTS="${JAVA_OPTS} -Dmaster.or.slave=master -Dcom.sun.management.jmxremote.port=${MASTER_JMX_PORT} -Xloggc:/data/applogs/swallow/swallow-consumerserver-master-gc.log"
SLAVE_JAVA_OPTS="${JAVA_OPTS} -Dmaster.or.slave=slave -Dcom.sun.management.jmxremote.port=${SLAVE_JMX_PORT} -Xloggc:/data/applogs/swallow/swallow-consumerserver-slave-gc.log"
MASTER_CLASS="com.dianping.swallow.consumerserver.bootstrap.MasterBootStrap"
SLAVE_CLASS="com.dianping.swallow.consumerserver.bootstrap.SlaveBootStrap"


if [ "$MODE" == "master" ]; then
    STD_OUT="/data/applogs/swallow/swallow-consumerserver-master-std.out"
    MASTER_JAVA_OPTS="${MASTER_JAVA_OPTS} -DmasterIp=$LOCAL_IP"
    log "Starting as master(masterIp is $LOCAL_IP ) ..."
    log "Output: $STD_OUT"
    exec java $MASTER_JAVA_OPTS  $MASTER_CLASS > "$STD_OUT" 2>&1 &
elif [ "$MODE" == "slave" ]; then
    if [ "$MASTER_IP" != "" ]; then
       log "MASTER_IP option: $MASTER_IP"
       echo $MASTER_IP |grep "^[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}\.[0-9]\{1,3\}$" > /dev/null
       if [ $? == 1 ]; then
          log "'$MASTER_IP' is an illegal ip address!"
          usage
          exit 1
       fi
       SLAVE_JAVA_OPTS="${SLAVE_JAVA_OPTS} -DmasterIp=$MASTER_IP"
    else
       log "     No masterIp option!"
       log "     use '$0 start/restart/stop slave [<masterIp>]' as slave."
       exit 1
    fi
    STD_OUT="/data/applogs/swallow/swallow-consumerserver-slave-std.out"
    log "Starting as slave ..."
    log "Output: $STD_OUT"
    exec java $SLAVE_JAVA_OPTS $SLAVE_CLASS > "$STD_OUT" 2>&1 &
else
    log "Your input is not corrent!"
    usage
    exit 1
fi
###########  检查是否启动成功 ############

log "Sleeping $SleepTime sec for waiting process started ..."


if [ "$MODE" == "master" ]; then
    SuccessLog="Server started at port "
else
    SuccessLog="start to wait $MASTER_IP master stop beating"
fi

SleepTime=10
mysleep $SleepTime
checkLog "$SuccessLog" $STD_OUT 

Pid=$(jps |grep $ProcessName |cut -d\  -f1)

if [ -n "$Pid" ]; then
	log "$ProcessName started as PID $Pid."
else
	log "start failed, none process exist."
	exit 1
fi

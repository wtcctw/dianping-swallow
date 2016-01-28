base_dir=$(dirname $0)

local_host="`hostname --fqdn`"
export local_ip=`host $local_host 2>/dev/null | grep "has address" | awk '{print $NF}'`

sh swallow_prepare.sh

export KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote.authenticate=false  -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=$local_ip -Dcom.sun.management.jmxremote=true"
unset local_ip
export JMX_PORT=9999

export LOG_DIR=/data/applogs/kafka
export KAFKA_HEAP_OPTS="-Xms4g -Xmx4g -XX:PermSize=48m -XX:MaxPermSize=48m"
export KAFKA_JVM_PERFORMANCE_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+DisableExplicitGC -Djava.awt.headless=true"


kpid=`jps | grep -w "Kafka" | awk '{print $1}'` 

if [ "$kpid" != "" ]; then

    before=$(($(date +%s%N)/1000000))

    kill -SIGTERM $kpid &
    wait $!
    echo "kill kafka server with pid $kpid"

    middle=$(($(date +%s%N)/1000000))
    echo "kill kafka server with "$(($middle-$before))" ms"

    closed=`netstat -ant|grep 9092 | wc -l`
    while [ "$closed" -ne 0 ]
    do
        sleep 0.01
        closed=`netstat -ant|grep 9092 | wc -l`
    done

    after=$(($(date +%s%N)/1000000))

    echo "connection all closed after "$(($after-$middle))" ms"
fi

$base_dir/kafka-server-start.sh -daemon $base_dir/../config/server.properties
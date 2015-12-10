base_dir=$(dirname $0)

sh swallow_prepare.sh

export LOG_DIR=/data/applogs/kafka
export KAFKA_HEAP_OPTS="-Xms4g -Xmx4g -XX:PermSize=48m -XX:MaxPermSize=48m"
export KAFKA_JVM_PERFORMANCE_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+DisableExplicitGC -Djava.awt.headless=true"

$base_dir/kafka-server-start.sh -daemon $base_dir/../config/server.properties

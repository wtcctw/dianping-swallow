
export LOG_DIR=/data/applogs/kafka
KAFKA_HOME=../kafka/kafka_2.9.1-0.8.2.2

function grepFun {
	RESULT=`echo $1 | sed 's/\([a-zA-Z]\+\)\([a-zA-Z]\)/\1[\2]/'`;
	echo $RESULT
}

function exist {
	PROCESS=`grepFun $1`
	count=`ps -ef | grep $PROCESS | wc -l`
	if [ $count -ge 1 ];then
		return 0
	fi
	
	return 1
}

function stop {
	
	PROCESS=`grepFun $1`
	ps -ef | grep $PROCESS | awk '{print $2}' | xargs kill -9;
}

function makeLocal {
	echo =======change appenv=========
	sed -i 's/alpha.lion.dp/127.0.0.1/' /data/webapps/appenv

	echo =======stop haproxy==========
	stop haproxy

	if ! `exist QuorumPeerMain`; then
		echo =======start zk============== 
		echo "nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &"
		nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties > /tmp/zk.log 2>&1 &
	else
		echo =======zk already start==============
	fi

	echo ======start kafka=========
	if ! `exist Kafka` ;then
		nohup $KAFKA_HOME/bin/kafka-server-start.sh  $KAFKA_HOME/config/server.properties  > /tmp/kafka.log 2>&1 &
	else 
        echo =======kafka already start==============
	fi

}

function makeNormal {
	echo =======change appenv=========
	sed -i 's/127.0.0.1/alpha.lion.dp/' /data/webapps/appenv

	echo =======stop zk============== 
	stop QuorumPeerMain
	
	echo ======stop kafka=========
	stop Kafka

	echo =======start haproxy==========
	sleep 1
	haproxy -D -f  haproxy.cfg
}

if [ "$1x" = 'x' ]; then
	makeLocal
fi

if [ "$1" = "normal" ]; then
	makeNormal
fi

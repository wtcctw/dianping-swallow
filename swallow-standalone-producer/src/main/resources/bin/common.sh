
function log {
	echo "[`date +'%m-%d %H:%M:%S'`] [INFO] $@"
}

getmem(){
    MEM=2G
    if hash free 2>/dev/null; then
        MEM=`free -m | grep 'Mem' | awk '{print int($2*2/3/1000)"G";}'`
    fi  
    echo $MEM
}

checkLog(){
	checkResult=$(grep "$1" $2 |wc -l)
	if [ $checkResult -ge 1 ]
	        then
        	log "Log ok"
	else
        	log "Can not find success log \"$SuccessLog\" "
        	tail -n 10 $2
		exit 1
	fi
}




DOCKIP=""
if [[ ${!#} =~ ^-Dhost.ip  ]]; then
    DOCKIP=${!#}
fi

JAVA_OPTS=$DOCKIP

if [ ! -d "/data/applogs/swallow" ] ; then
  mkdir -p "/data/applogs/swallow"
fi


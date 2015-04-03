
getmem(){
    MEM=2G
    if hash free 2>/dev/null; then
        MEM=`free -m | grep 'Mem' | awk '{print int($2*2/3/1000)"G";}'`
    fi  
    echo $MEM
}


DOCKIP=""
if [[ ${!#} =~ ^-Dhost.ip  ]]; then
    DOCKIP=${!#}
fi

JAVA_OPTS=$DOCKIP

if [ ! -d "/data/applogs/swallow" ] ; then
  mkdir -p "/data/applogs/swallow"
fi


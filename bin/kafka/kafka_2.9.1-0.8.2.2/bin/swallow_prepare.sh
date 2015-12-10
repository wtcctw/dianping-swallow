#
base_dir=$(dirname $0)
env=`grep env /data/webapps/appenv |  awk -F'=|\r' '{print $2}' `
if [ "x$env" = "x" ]; then
	echo can not find env
	exit 1
fi

EnvConfigFile="$base_dir/../config/server.properties.$env"
ConfigFile="$base_dir/../config/server.properties"
if [ ! -f $EnvConfigFile ]; then
	echo "$EnvConfigFile not found, may be already installed"
else
	mv $EnvConfigFile $ConfigFile
fi


local_host="`hostname --fqdn`"
local_ip=`host $local_host 2>/dev/null | awk '{print $NF}'`

sed -i "s/advertised.host.name=\(.*\)/advertised.host.name=$local_ip/" $ConfigFile

host=`hostname`
id=`expr match "$host" '[^1-9]\+\([1-9]\+\)' `

sed -i "s/broker.id=\(.*\)/broker.id=$id/" $ConfigFile

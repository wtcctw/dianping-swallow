#!/bin/bash
source /etc/profile
FtpServer="ftp://10.1.1.189/arch/"
Datedir=$(date +%Y%m%d)
Sfile="swallow-producerserver-withDependencies.tar"
FileFtpAddress="$FtpServer$Datedir/dev/$Sfile"
echo "file address : $FileFtpAddress"
SerLog="/data/applogs/swallow/swallow-producerserver-std.out"
Jobdir="/home/wukezhu"
Jobname="swallow-producerserver"


cd $Jobdir


#下载，备份，解压
echo "================ downloading, backup and tar file ================"
rm -f $Sfile
wget $FtpServer$Datedir/dev/$Sfile
rm -rf $Jobname-backup && cp -r $Jobname $Jobname-backup
rm -rf $Jobname && mkdir $Jobname && tar xf $Sfile -C $Jobname


cd $Jobdir/$Jobname

#关闭进程
echo "========================     shutdowning   ========================"
Pid=$(jps |grep ProducerServerBootstrap |cut -d\  -f1)
        echo  "PID $Pid is running! Kill it ..."
        kill $Pid
echo "sleep 15 sec ..."
sleep 15
#确保进程已经关闭
PidCount=$(jps |grep ProducerServerBootstrap|wc -l)
while [ $PidCount -ge 1 ]
  do
        Pid=$(jps |grep ProducerServerBootstrap |cut -d\  -f1)
        echo  "PID $Pid is still running! Kill it ..."
        kill -9  $Pid
	PidCount=$(jps |grep ProducerServerBootstrap|wc -l)
  done

echo "swallow.producerserver.bootstrap is stop"


echo "=============================== starting ========================="
sh start.sh
echo "sleeping 32 sec ..."
sleep 32

Pid=$(jps |grep ProducerServerBootstrap |cut -d\  -f1)
echo  "PID $Pid is running!"

SerStLogn=$(grep "Producer service for client is ready" $SerLog |wc -l)

if [ $SerStLogn -ge 1 ]
        then
        grep "Producer service for client is ready" $SerLog
        echo "Swallow Producerserver Server started sucessfully,$SerStLogn"
else
        echo "Swallow Producerserver starts error! "
        echo "Server:$SerStLogn"
        tail -n 10 $SerLog
fi

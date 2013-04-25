#!/bin/bash
source /etc/profile
FtpServer="ftp://10.1.1.189/arch/"
Datedir=$(date +%Y%m%d)
Sfile="swallow-consumerserver-withDependencies.tar"
FileFtpAddress="$FtpServer$Datedir/dev/$Sfile"
#echo "file address : $FileFtpAddress"
SerLog="/data/applogs/swallow/swallow-consumerserver-slave-std.out"
Jobdir="/home/wukezhu"
Jobname="swallow-consumerserver-slave"
MasterIp="192.168.8.21"

cd $Jobdir


#下载，备份，解压
#rm -f $Sfile
#wget $FtpServer$Datedir/dev/$Sfile
echo "============================= backup and tar file ========================="
rm -rf $Jobname-backup && cp -r $Jobname $Jobname-backup
rm -rf $Jobname && mkdir $Jobname && tar xf $Sfile -C $Jobname


cd $Jobdir/$Jobname

#关闭进程
echo "=============================== shutdowning ==============================="
echo shutdown | telnet localhost 17556
echo "sleep 15 sec ..."
sleep 15
#确保进程已经关闭
SlavePidCount=$(jps |grep SlaveBootStrap|wc -l)
while [ $SlavePidCount -ge 1 ]
  do
        SlavePid=$(jps |grep SlaveBootStrap |cut -d\  -f1)
        echo  "PID $SlavePid is running! Kill it ..."
        kill -9  $SlavePid
	SlavePidCount=$(jps |grep SlaveBootStrap|wc -l)
  done

echo "swallow.consumerserver.bootstrap is stop"


echo "================================== starting ==============================="
sh start.sh slave $MasterIp
echo "sleeping 32 sec ..."
sleep 32

SlavePid=$(jps |grep SlaveBootStrap |cut -d\  -f1)
echo  "PID $SlavePid is running!"


SerStLogn=$(grep "start to wait $MasterIp master stop beating" $SerLog |wc -l)

if [ $SerStLogn -ge 1 ]
        then
        grep "start to wait $MasterIp master stop beating" $SerLog
        echo "Swallow Consumerserver-Slave Server started sucessfully,$SerStLogn"
else
        echo "Swallow Consumerserver-Slave starts error! "
        echo "Server:$SerStLogn"
        tail -n 10 $SerLog
fi

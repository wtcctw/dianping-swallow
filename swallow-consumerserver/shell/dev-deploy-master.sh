#!/bin/bash
source /etc/profile
FtpServer="ftp://10.1.1.189/arch/"
Datedir=$(date +%Y%m%d)
Sfile="swallow-consumerserver-withDependencies.tar"
FileFtpAddress="$FtpServer$Datedir/dev/$Sfile"
echo "file address : $FileFtpAddress"
SerLog="/data/applogs/swallow/swallow-consumerserver-master-std.out"
Jobdir="/home/wukezhu"
Jobname="swallow-consumerserver-master"


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
echo shutdown | telnet localhost 17555
echo "sleep 15 sec ..."
sleep 15
#确保进程已经关闭
MasterPidCount=$(jps |grep MasterBootStrap|wc -l)
while [ $MasterPidCount -ge 1 ]
  do
        MasterPid=$(jps |grep MasterBootStrap |cut -d\  -f1)
        echo  "PID $MasterPid is running! Kill it ..."
        kill -9  $MasterPid
	MasterPidCount=$(jps |grep MasterBootStrap|wc -l)
  done

echo "swallow.consumerserver.bootstrap is stop"


echo "=============================== starting ========================="
sh start.sh master
echo "sleeping 32 sec ..."
sleep 32

MasterPid=$(jps |grep MasterBootStrap |cut -d\  -f1)
echo  "PID $MasterPid is running!"

SerStLogn=$(grep "Server started at port 8081" $SerLog |wc -l)

if [ $SerStLogn -ge 1 ]
        then
        grep "Server started at port 8081" $SerLog
        echo "Swallow Consumerserver-Master Server started sucessfully,$SerStLogn"
else
        echo "Swallow Consumerserver-Master starts error! "
        echo "Server:$SerStLogn"
        tail -n 10 $SerLog
fi

#!/bin/bash
#这是群英写的dev环境的一键关闭/部署/重启的脚本，线上的和这个有修改。
source /etc/profile
FtpServer="ftp://10.1.1.189/arch/"
Datedir=$(date +%Y%m%d)
Sfile="swallow-consumerserver-0.6.5-withDependencies.tar"
echo "$FtpServer$Datedir/$Sfile"
echo "ftp://10.1.1.189/arch/20130311/swallow-consumerserver-0.6.5-withDependencies.tar"
SerLog="/data/applogs/swallow/swallow-consumerserver-slave-std.out"
Jobdir="/home/wukezhu"
Jobname="swallow-consumerserver-slave"
MasterIp="127.0.0.1"
TelPort="17556"

cd $Jobdir
echo shutdown | telnet localhost $TelPort

sleep 20
#rm -rf swallow-consumerserver-slave && cp -a  swallow-consumerserver-slave-backup  swallow-consumerserver-slave
#wget $FtpServer$Datedir/$Sfile
#rm -rf $Jobname && mkdir $Jobname && tar xvf $Sfile -C $Jobname

cd $Jobdir/$Jobname

SlavePidCount=$(jps |grep SlaveBootStrap|wc -l)
if [ $SlavePidCount -ge 1 ] 
    then
    SlavePid=$(jps |grep SlaveBootStrap |cut -d\  -f1)
    echo  "PID $SlavePid is running!"
    kill -9  $SlavePid
else
    echo "swallow.consumerserver.Slave.bootstrap is stop"
fi

sh start.sh slave $MasterIp
sleep 40
SerStLogn=$(grep "start to wait $MasterIp master stop beating" $SerLog |wc -l)

if [ $SerStLogn -ge 1 ] 
    then
    grep "Server started at port 8081" $SerLog
    echo "Swallow Consumerserver-slave Server started sucessfully,$SerStLogn"
else 
    echo "Swallow Consumerserver-slave starts error! "
    echo "Server:$SerStLogn"
    tail -n 20 $SerLog 
fi

#cd $Jobdir/backup/ ; [ $(ls |wc -l) -ge 7] && rm -rf $(ls |head -n1)
#rm $Sfile && echo "$Sfile is deleted!"
#ls -l && pwd

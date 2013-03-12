#!/bin/bash
#这是群英写的dev环境的一键关闭/部署/重启的脚本，线上的和这个有修改。
source /etc/profile
FtpServer="ftp://10.1.1.189/arch/"
Datedir=$(date +%Y%m%d)
Sfile="swallow-consumerserver-0.6.5-withDependencies.tar"
echo "$FtpServer$Datedir/$Sfile"
echo "ftp://10.1.1.189/arch/20130311/swallow-consumerserver-0.6.5-withDependencies.tar"
SerLog="/data/applogs/swallow/swallow-consumerserver-master-std.out"
Jobdir="/home/wukezhu"
Jobname="swallow-consumerserver-master"

cd $Jobdir
echo shutdown | telnet localhost 17555

sleep 15
rm -rf swallow-consumerserver-master && cp -a  swallow-consumerserver  swallow-consumerserver-master
#wget $FtpServer$Datedir/$Sfile
#rm -rf $Jobname && mkdir $Jobname && tar xvf $Sfile -C $Jobname

cd $Jobdir/$Jobname

MasterPidCount=$(jps |grep MasterBootStrap|wc -l)
if [ $MasterPidCount -ge 1 ] 
    then
    MasterPid=$(jps |grep MasterBootStrap |cut -d\  -f1)
    echo  "PID $MasterPid is running!"
    kill -9  $MasterPid
else
    echo "swallow.consumerserver.bootstrap is stop"
fi


sh start.sh master
sleep 32
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

#cd $Jobdir/backup/ ; [ $(ls |wc -l) -ge 7] && rm -rf $(ls |head -n1)
#rm $Sfile && echo "$Sfile is deleted!"

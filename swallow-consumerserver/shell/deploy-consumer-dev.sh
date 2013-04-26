#!/bin/bash
##########################################################################################################################################
#  dev的consumerserver部署方法：
#  (1)打包tar之后，放到ftp，路径为ftp://10.1.1.189/arch/<当天日期>/swallow-consumerserver-withDependencies.tar
#  (2)到192.168.8.21机器的/home/wukezhu/目录下，执行sh deploy-consumer-dev.sh即可。
#
##########################################################################################################################################
source /etc/profile
FtpServer="ftp://10.1.1.189/arch"
Datedir=$(date +%Y%m%d)
TarFile="swallow-consumerserver-withDependencies.tar"
Mode="dev"
FileFtpAddress="$FtpServer/$Datedir/$Mode/$TarFile"
UserDir="/home/wukezhu"
MasterDir="swallow-consumerserver-master"
SlaveDir="swallow-consumerserver-slave"

function pause(){
   read -p "$*"
}

cd $UserDir

#下载，备份，解压
echo "================ Downloading, backup and tar file ================"
rm -f $TarFile
echo "Downloading file : $FileFtpAddress"
wget $FileFtpAddress
rm -rf $MasterDir-backup && cp -r $MasterDir $MasterDir-backup
rm -rf $MasterDir && mkdir $MasterDir && tar xf $TarFile -C $MasterDir
echo "Backuped master dir."
rm -rf $SlaveDir-backup && cp -r $SlaveDir $SlaveDir-backup
rm -rf $SlaveDir && mkdir $SlaveDir && tar xf $TarFile -C $SlaveDir
echo "Backuped master dir."

#重启master
echo $
echo "========== To restart master =========="
pause 'Press [Enter] key to continue...'
cd $UserDir/$MasterDir
sh swallow.sh restart master

#重启slave
echo $
echo "========== To restart slave =========="
pause 'Press [Enter] key to continue...'
cd $UserDir/$SlaveDir
sh swallow.sh restart slave 192.168.8.21

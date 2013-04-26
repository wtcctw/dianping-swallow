#!/bin/bash
##########################################################################################################################################
#  dev的consumerserver部署方法：
#  (1)打包tar之后，放到ftp，路径为ftp://10.1.1.189/arch/<当天日期>/<Mode>/swallow-consumerserver-withDependencies.tar
#  (2)到相应机器(dev/alpha/beta)的 $AppRootDir 目录下，执行sh deploy-consumer.sh <Mode>即可。
#
##########################################################################################################################################


##################################################
# Some utility functions
##################################################
usage(){
    echo "  Usage:"
    echo "     use '$0 dev/alpha/qa'."
    exit 1
}
pause(){
   read -p "$*"
}

#环境变量
source /etc/profile
FtpServer="ftp://10.1.1.189/arch"
Datedir=$(date +%Y%m%d)
TarFile="swallow-consumerserver-withDependencies.tar"
Mode=$1
FileFtpAddress="$FtpServer/$Datedir/$Mode/$TarFile"
if [ "$Mode" = "dev" ]; then
    UserDir="/home/wukezhu"
elif [ "$Mode" = "alpha" -o "$Mode" = "qa" ]; then
    UserDir="/data/swallow"
else
    echo "Your input is not corrent!"
    usage
    exit 1
fi
MasterDir="swallow-consumerserver-master"
SlaveDir="swallow-consumerserver-slave"

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
echo ""
echo "========== To restart master =========="
pause 'Press [Enter] key to continue...'
cd $UserDir/$MasterDir
sh swallow.sh restart master

#重启slave
echo ""
echo "========== To restart slave =========="
pause 'Press [Enter] key to continue...'
cd $UserDir/$SlaveDir
sh swallow.sh restart slave 192.168.8.21

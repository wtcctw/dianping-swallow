#!/bin/bash
##########################################################################################################################################
#  dev的producerserver部署方法：
#  (1)打包tar之后，放到ftp，路径为ftp://10.1.1.189/arch/<当天日期>/<Mode>/swallow-producerserver-withDependencies.tar
#  (2)到相应机器(dev/alpha/beta)的 $AppRootDir 目录下，执行sh deploy-producer.sh <Mode>即可。
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
TarFile="swallow-producerserver-withDependencies.tar"
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
AppDir="swallow-producerserver"

cd $UserDir

#下载，备份，解压
echo "================ Downloading, backup and tar file ================"
rm -f $TarFile
echo "Downloading file : $FileFtpAddress"
wget $FileFtpAddress
rm -rf $AppDir-backup && cp -r $AppDir $AppDir-backup
rm -rf $AppDir && mkdir $AppDir && tar xf $TarFile -C $AppDir
echo "Backuped producer dir."

#重启
echo ""
pause 'Press [Enter] key to restart producer ...'
cd $UserDir/$AppDir
sh swallow.sh restart

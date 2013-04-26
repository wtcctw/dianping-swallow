#!/bin/bash
##########################################################################################################################################
#  dev的producerserver部署方法：
#  (1)打包tar之后，放到ftp，路径为ftp://10.1.1.189/arch/<当天日期>/swallow-producerserver-withDependencies.tar
#  (2)到192.168.8.22机器的/home/wukezhu/目录下，执行sh deploy-producer-dev.sh即可。
#
##########################################################################################################################################
source /etc/profile
FtpServer="ftp://10.1.1.189/arch"
Datedir=$(date +%Y%m%d)
TarFile="swallow-producerserver-withDependencies.tar"
Mode="dev"
FileFtpAddress="$FtpServer/$Datedir/$Mode/$TarFile"
UserDir="/home/wukezhu"
AppDir="swallow-producerserver"

function pause(){
   read -p "$*"
}

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
echo $
echo "========== To restart =========="
pause 'Press [Enter] key to continue...'
cd $UserDir/$AppDir
sh swallow.sh restart

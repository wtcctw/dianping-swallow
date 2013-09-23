#!/bin/bash
##########################################################################################################################################
#  在dev/alpha/qa环境下的producerserver部署方法(product环境使用群英的脚本，它会遍历多台swallow机器批量发布)：
#  (1)打包tar之后，放到ftp，路径为ftp://10.1.1.189/arch/<当天日期>/<Mode>/swallow-producerserver-withDependencies.tar
#  (2)到相应机器(dev/alpha/qa)的 $AppRootDir 目录下，执行sh deploy-producer.sh <Mode>即可。
#
##########################################################################################################################################

##################################################
# Some utility functions
##################################################
usage(){
    echo "  Usage:"
    echo "     use '$0 alpha/qa/prodcut'."
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
FileFtpAddress="$FtpServer/$Datedir/$TarFile"
if [ "$Mode" = "product" ]; then
    UserDir="/data"
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
rm -rf backup/$AppDir && cp -r $AppDir backup/$AppDir
rm -rf $AppDir && mkdir $AppDir && tar xf $TarFile -C $AppDir
cp appenv $AppDir
echo "Backuped producer dir."

#重启
echo ""
pause 'Press [Enter] key to restart producer ...'
cd $UserDir/$AppDir
sh swallow.sh restart
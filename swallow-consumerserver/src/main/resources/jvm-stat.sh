#!/bin/bash
StatDir="/data/swallow"

if [ ! -d "$StatDir" ] ; then
  mkdir -p "$StatDir"
fi

jvmStat(){
  Pid=$1
  ProcessName=$2
  echo -e "======= Executing jstack \033[32m$ProcessName\033[0m(pid=$Pid) to $StatDir/$ProcessName.jstack.out..."
  jstack $Pid > $StatDir/$ProcessName.jstack.out
  echo -e "======= Executing jmap -heap \033[32m$ProcessName\033[0m(pid=$Pid) to $StatDir/$ProcessName.jmap-heap.out..."
  jmap -heap $Pid > $StatDir/$ProcessName.jmap-heap.out
  echo -e "======= Executing jmap -histo \033[32m$ProcessName\033[0m(pid=$Pid) to $StatDir/$ProcessName.jmap-histo.out ..."
  jmap -histo $Pid > $StatDir/$ProcessName.jmap-histo.out
}

for i in MasterBootStrap SlaveBootStrap ProducerServerBootstrap
  do
   PidCount=$(jps |grep $i|wc -l)
   if [ $PidCount -ge 1 ]; then
     Pid=$(jps |grep $i |cut -d\  -f1)
     echo -e "======= finded Process name \033[32m$i\033[0m, pid is $Pid"
     ProcessName=$i
     jvmStat $Pid $ProcessName
     echo ''
   fi
done
#!/bin/bash
StatDir="/data/swallow"

if [ ! -d "$StatDir" ] ; then
  mkdir -p "$StatDir"
fi

jvmStat(){
  Pid=$1
  ProcessName=$2
  echo "Now jstacking $ProcessName(pid=$Pid) to $StatDir/$ProcessName.jstack.out..."
  jstack $Pid > $StatDir/$ProcessName.jstack.out
  echo "Now jmap -heap $ProcessName(pid=$Pid) to $StatDir/$ProcessName.jmap-heap.out..."
  jmap -heap $Pid > $StatDir/$ProcessName.jmap-heap.out
  echo "Now jmap -histo $ProcessName(pid=$Pid) to $StatDir/$ProcessName.jmap-histo.out ..."
  jmap -histo $Pid > $StatDir/$ProcessName.jmap-histo.out
}

for i in MasterBootStrap SlaveBootStrap ProducerServerBootstrap
  do
 PidCount=$(jps |grep $i|wc -l)
 if [ $PidCount -ge 1 ]; then
   Pid=$(jps |grep $i |cut -d\  -f1)
   echo "finded Process name $i, pid is $Pid"
   ProcessName=$i
   jvmStat $Pid $ProcessName
 fi
done

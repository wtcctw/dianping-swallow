import ConfigParser
import sets
from bson.timestamp import Timestamp

class MonitorConfig:
    #Mongouris
    mongoMongoUri = sets.Set()
    #Domains
    domainConsumerAccept = sets.Set()
    domainProducerReject = sets.Set()
    #Receivers
    mailMailReceiver = sets.Set()
    smsSmsReceiver = sets.Set()
    #TopicNames ignore-set for sms sending
    smsignoreTopicNames = dict()
    #Consumer server monitor
    consumerServersIp = sets.Set()
    #Alarm upper limit
    alarmMailDelay = int()#For red tag
    alarmSmsProduceFailed = int()
    alarmSmsMongoFailed = int()
    alarmSmsCumulateAsync = int()
    alarmSmsCumulateSum = int()
    alarmSmsDelaySum = int()
    #Argus from log
    logPreProduceFailed = int()
    logPreSaveFailed = int()
    logPreAsyncCumulated = int()
    logPreSumCumulated = int()
    logPreSumDelay = int()

    logLastSmsTime = int()
    
    logMongoTopic = dict()
    logMongoConsumeStatus = dict()
    
    def __init__(self):
        self.getConfig()
        self.getLog()
    
    def getConfig(self):
        configParser = ConfigParser.ConfigParser()
        fpConfig = open('/data/home/workcron/qing.gu/swallow-monitor/monitor.config', 'rb')
        configParser.readfp(fpConfig)
        
        self.mongoMongoUri = str(configParser.get('mongo', 'mongoUri')).split(';')
        
        self.domainConsumerAccept = str(configParser.get('domain', 'consumerAccept')).split(';')
        self.domainProducerReject = str(configParser.get('domain', 'producerReject')).split(';')
        
        self.mailMailReceiver = str(configParser.get('mail', 'mailReceiver')).split(';')
        
        self.smsSmsReceiver = str(configParser.get('sms', 'smsReceiver')).split(';')
        
        smsIgnoreStrs = str(configParser.get('sms-ignore', 'topicNames')).split(';')
        for smsIgnoreStr in smsIgnoreStrs:
            if len(str(smsIgnoreStr).strip()) == 0:
                continue;
            ignoreInfos = smsIgnoreStr.split(',')
            if len(ignoreInfos) == 1:
                self.smsignoreTopicNames[ignoreInfos[0].strip()] = list()
            if len(ignoreInfos) > 1:
                topicName = ignoreInfos[0]
                consumers = ignoreInfos[1:]
                self.smsignoreTopicNames[topicName] = consumers
        
        self.consumerServersIp = str(configParser.get('consumer', 'consumerServersIp')).split(';')
        
        self.alarmMailDelay = int(configParser.get('alarm', 'mailDelay')) * 60 if configParser.get('alarm', 'mailDelay') != '' else 10 * 60
        self.alarmSmsProduceFailed = int(configParser.get('alarm', 'smsProduceFailed')) if configParser.get('alarm', 'smsProduceFailed') != '' else 10
        self.alarmSmsMongoFailed = int(configParser.get('alarm', 'smsMongoFailed')) if configParser.get('alarm', 'smsMongoFailed') != '' else 10
        self.alarmSmsCumulateAsync = int(configParser.get('alarm', 'smsCumulateAsync')) if configParser.get('alarm', 'smsCumulateAsync') != '' else 1000
        self.alarmSmsCumulateSum = int(configParser.get('alarm', 'smsCumulateSum')) if configParser.get('alarm', 'smsCumulateSum') != '' else 10000
        self.alarmSmsDelaySum = int(configParser.get('alarm', 'smsDelaySum')) * 60 if configParser.get('alarm', 'smsDelaySum') != '' else 60 * 60

        fpConfig.close()
        
    def getLog(self):
        configParser = ConfigParser.ConfigParser()
        configParser.read('/data/home/workcron/qing.gu/swallow-monitor/monitor.log')
        
        sessions = configParser.sections()
        if sessions.__contains__('alarm'):
            self.logPreAsyncCumulated = configParser.getint('alarm', 'asynccumulated')
            self.logPreProduceFailed = configParser.getint('alarm', 'producefailed')
            self.logPreSaveFailed = configParser.getint('alarm', 'savefailed')
            self.logPreSumCumulated = configParser.getint('alarm', 'sumcumulated')
            self.logPreSumDelay = configParser.getint('alarm', 'sumdelay')
            self.logLastSmsTime = configParser.getint('alarm', 'logLastSmsTime')
        else:
            self.logPreAsyncCumulated = 0
            self.logPreProduceFailed = 0
            self.logPreSaveFailed = 0
            self.logPreSumCumulated = 0
            self.logPreSumDelay = 0
            self.logLastSmsTime = 0

        if sessions.__contains__('mongo'):
            logMongoTopicList = configParser.items('mongo')
            for topicName, messageId in logMongoTopicList:
                timeAndInc = messageId.split(',')
                mid = Timestamp(int(timeAndInc[0]), int(timeAndInc[1]))
                self.logMongoTopic[topicName] = mid
        
        for session in sessions:
            if str(session).startswith('consume-'):
                consumeItems = configParser.items(session)
                topicName = str(session)[8:]
                consumerDict = dict()
                for option, value in consumeItems:
                    timeAndInc = str(value).split(',')
                    consumerDict[option] = Timestamp(int(timeAndInc[0]), int(timeAndInc[1]))
                self.logMongoConsumeStatus[topicName] = consumerDict
                
    
    def updateLog(self):
        configParser = ConfigParser.ConfigParser()
        
        if not configParser.has_section('alarm'):
            configParser.add_section('alarm')
        if not configParser.has_section('mongo'):
            configParser.add_section('mongo')
            
        configParser.set('alarm', 'asynccumulated', str(self.logPreAsyncCumulated))
        configParser.set('alarm', 'producefailed', str(self.logPreProduceFailed))
        configParser.set('alarm', 'savefailed', str(self.logPreSaveFailed))
        configParser.set('alarm', 'sumcumulated', str(self.logPreSumCumulated))
        configParser.set('alarm', 'sumdelay', str(self.logPreSumDelay))
        configParser.set('alarm', 'logLastSmsTime', str(self.logLastSmsTime))
        
        for topicName, messageId in self.logMongoTopic.items():
            configParser.set('mongo', topicName, str(messageId.time) + ',' + str(messageId.inc))
        
        for topicName, consumerDict in self.logMongoConsumeStatus.items():
            if not configParser.has_section('consume-' + topicName):
                configParser.add_section('consume-' + topicName)
            for consumerId, value in consumerDict.items():
                configParser.set('consume-' + topicName, consumerId, str(value.time) + ',' + str(value.inc))
        
        fpLog = open('/data/home/workcron/qing.gu/swallow-monitor/monitor.log', 'w')
        configParser.write(fpLog)
        fpLog.close()

    def __repr__(self):
        return    '[mongo]      mongoUri=' + self.mongoMongoUri.__repr__() + \
                '\n[domain]     consumerAccept=' + self.domainConsumerAccept.__repr__() + \
                '\n[domain]     producerReject=' + self.domainProducerReject.__repr__() + \
                '\n[mail]       mailReceiver=' + self.mailMailReceiver.__repr__() + \
                '\n[sms]        smsReceiver=' + self.smsSmsReceiver.__repr__() + \
                '\n[sms-ignore] topicNames=' + self.smsignoreTopicNames.__repr__() + \
                '\n[alarm]      mailDelay=' + str(self.alarmMailDelay) + \
                '\n[alarm]      smsProduceFailed=' + str(self.alarmSmsProduceFailed) + \
                '\n[alarm]      smsMongoFailed=' + str(self.alarmSmsMongoFailed) + \
                '\n[alarm]      smsCumulateAsync=' + str(self.alarmSmsCumulateAsync) + \
                '\n[alarm]      smsCumulateSum=' + str(self.alarmSmsCumulateSum) + \
                '\n[alarm]      smsDelaySum=' + str(self.alarmSmsDelaySum) + \
                '\n[alarm]      lastSmsTime=' + str(self.logLastSmsTime)

var = MonitorConfig()
print var

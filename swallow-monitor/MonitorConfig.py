# MonitorConfig.py
# coding=gbk
import ConfigParser
import sets
from bson.timestamp import Timestamp

class MonitorConfig:
    #配置引用对象
    global configParser
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
    
    logMongoTopic = dict()
    logMongoConsumeStatus = dict()
    
    def __init__(self):
        self.getConfig()
        self.getLog()
    
    def getConfig(self):
        MonitorConfig.configParser = ConfigParser.ConfigParser()
        fpConfig = open('monitor.config', 'rb')
        MonitorConfig.configParser.readfp(fpConfig)
        
        self.mongoMongoUri = str(MonitorConfig.configParser.get('mongo', 'mongoUri')).split(';')
        
        self.domainConsumerAccept = str(MonitorConfig.configParser.get('domain', 'consumerAccept')).split(';')
        self.domainProducerReject = str(MonitorConfig.configParser.get('domain', 'producerReject')).split(';')
        
        self.mailMailReceiver = str(MonitorConfig.configParser.get('mail', 'mailReceiver')).split(';')
        
        self.smsSmsReceiver = str(MonitorConfig.configParser.get('sms', 'smsReceiver')).split(';')
        
        smsIgnoreStrs = str(MonitorConfig.configParser.get('sms-ignore', 'topicNames')).split(';')
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
        
        self.consumerServersIp = str(MonitorConfig.configParser.get('consumer', 'consumerServersIp')).split(';')
        
        self.alarmMailDelay = int(MonitorConfig.configParser.get('alarm', 'mailDelay')) * 60 if MonitorConfig.configParser.get('alarm', 'mailDelay') != '' else 10 * 60
        self.alarmSmsProduceFailed = int(MonitorConfig.configParser.get('alarm', 'smsProduceFailed')) if MonitorConfig.configParser.get('alarm', 'smsProduceFailed') != '' else 10
        self.alarmSmsMongoFailed = int(MonitorConfig.configParser.get('alarm', 'smsMongoFailed')) if MonitorConfig.configParser.get('alarm', 'smsMongoFailed') != '' else 10
        self.alarmSmsCumulateAsync = int(MonitorConfig.configParser.get('alarm', 'smsCumulateAsync')) if MonitorConfig.configParser.get('alarm', 'smsCumulateAsync') != '' else 1000
        self.alarmSmsCumulateSum = int(MonitorConfig.configParser.get('alarm', 'smsCumulateSum')) if MonitorConfig.configParser.get('alarm', 'smsCumulateSum') != '' else 10000
        self.alarmSmsDelaySum = int(MonitorConfig.configParser.get('alarm', 'smsDelaySum')) * 60 if MonitorConfig.configParser.get('alarm', 'smsDelaySum') != '' else 60 * 60
        
        fpConfig.close()
        
    def getLog(self):
#         MonitorConfig.configParser = ConfigParser.ConfigParser()
#         fpConfig = open('monitor.config', 'rb')
#         MonitorConfig.configParser.readfp(fpConfig)
        MonitorConfig.configParser.read(MonitorConfig.configParser.get('log', 'logfile'))
        
        sessions = MonitorConfig.configParser.sections()
        if sessions.__contains__('alarm'):
            self.logPreAsyncCumulated = MonitorConfig.configParser.getint('alarm', 'asynccumulated')
            self.logPreProduceFailed = MonitorConfig.configParser.getint('alarm', 'producefailed')
            self.logPreSaveFailed = MonitorConfig.configParser.getint('alarm', 'savefailed')
            self.logPreSumCumulated = MonitorConfig.configParser.getint('alarm', 'sumcumulated')
            self.logPreSumDelay = MonitorConfig.configParser.getint('alarm', 'sumdelay')
        else:
            self.logPreAsyncCumulated = 0
            self.logPreProduceFailed = 0
            self.logPreSaveFailed = 0
            self.logPreSumCumulated = 0
            self.logPreSumDelay = 0

        if sessions.__contains__('topic-messageid'):
            logMongoTopicList = MonitorConfig.configParser.items('topic-messageid')
            for topicName, messageId in logMongoTopicList:
                timeAndInc = messageId.split(',')
                mid = Timestamp(int(timeAndInc[0]), int(timeAndInc[1]))
                self.logMongoTopic[topicName] = mid
        
        for session in sessions:
            if str(session).startswith('consume-'):
                consumeItems = MonitorConfig.configParser.items(session)
                topicName = str(session)[8:]
                consumerDict = dict()
                for option, value in consumeItems:
                    timeAndInc = str(value).split(',')
                    consumerDict[option] = Timestamp(int(timeAndInc[0]), int(timeAndInc[1]))
                self.logMongoConsumeStatus[topicName] = consumerDict
                
    
    def updateLog(self):
#         MonitorConfig.configParser = ConfigParser.ConfigParser()
        
        if not MonitorConfig.configParser.has_section('alarm'):
            MonitorConfig.configParser.add_section('alarm')
        if not MonitorConfig.configParser.has_section('mongo'):
            MonitorConfig.configParser.add_section('mongo')
            
        MonitorConfig.configParser.set('alarm', 'asynccumulated', str(self.logPreAsyncCumulated))
        MonitorConfig.configParser.set('alarm', 'producefailed', str(self.logPreProduceFailed))
        MonitorConfig.configParser.set('alarm', 'savefailed', str(self.logPreSaveFailed))
        MonitorConfig.configParser.set('alarm', 'sumcumulated', str(self.logPreSumCumulated))
        MonitorConfig.configParser.set('alarm', 'sumdelay', str(self.logPreSumDelay))
        
        for topicName, messageId in self.logMongoTopic.items():
            MonitorConfig.configParser.set('mongo', topicName, str(messageId.time) + ',' + str(messageId.inc))
        
        for topicName, consumerDict in self.logMongoConsumeStatus.items():
            if not MonitorConfig.configParser.has_section('consume-' + topicName):
                MonitorConfig.configParser.add_section('consume-' + topicName)
            for consumerId, value in consumerDict.items():
                MonitorConfig.configParser.set('consume-' + topicName, consumerId, str(value.time) + ',' + str(value.inc))
        
        fpLog = open(MonitorConfig.configParser.get('log', 'logfile'), 'w')
        MonitorConfig.configParser.write(fpLog)
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
                '\n[alarm]      smsDelaySum=' + str(self.alarmSmsDelaySum)

var = MonitorConfig()
print var

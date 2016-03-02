package com.dianping.swallow.web.controller;

import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.controller.filter.FilterChainFactory;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.controller.filter.validator.*;
import com.dianping.swallow.web.controller.handler.HandlerChainFactory;
import com.dianping.swallow.web.controller.handler.config.*;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.controller.handler.lion.ConsumerServerLionHandler;
import com.dianping.swallow.web.controller.handler.lion.LionHandlerChain;
import com.dianping.swallow.web.controller.handler.lion.TopicCfgLionHandler;
import com.dianping.swallow.web.controller.handler.lion.TopicWhiteListLionHandler;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.filter.LogFilter;
import com.dianping.swallow.web.model.resource.TopicApplyResource;
import com.dianping.swallow.web.service.KafkaService;
import com.dianping.swallow.web.service.TopicApplyService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mingdongli
 *         2015年9月9日下午12:04:31
 */
@Controller
public class TopicApplyController extends AbstractSidebarBasedController {

    @Resource(name = "topicResourceService")
    private TopicResourceService topicResourceService;

    @Resource(name = "kafkaService")
    private KafkaService kafkaService;

    @Autowired
    private FilterChainFactory filterChainFactory;

    @Autowired
    private HandlerChainFactory handlerChainFactory;

    @Autowired
    private SwitchValidatorFilter switchValidatorFilter;

    @Autowired
    private NameValidatorFilter nameValidatorFilter;

    @Autowired
    private QuoteValidatorFilter quoteValidatorFilter;

    @Autowired
    private TypeValidatorFilter typeValidatorFilter;

    @Autowired
    private ApplicantValidatorFilter applicantValidatorFilter;

    @Autowired
    private MongoServerHandler mongoServerHandler;

    @Autowired
    private KafkaServerHandler kafkaServerHandler;

    @Autowired
    private ConsumerServerHandler consumerServerHandler;

    @Autowired
    private QuoteHandler quoteHandler;

    @Autowired
    private TopicWhiteListLionHandler topicWhiteListLionHandler;

    @Autowired
    private ConsumerServerLionHandler consumerServerLionHandler;

    @Autowired
    private TopicCfgLionHandler topicCfgLionHandler;

    private Object APPLY_TOPIC = new Object();

    @Resource(name = "topicApplyService")
    private TopicApplyService topicApplyService;

    @Value("${swallow.web.kafka.applytopic.partition}")
    private int N_PARTITION = 1;

    /*
    * 下面3个是qa环境的kafka配置
    * */
    @Value("${swallow.web.kafka.applytopic.qa.partition}")
    private int QA_N_PARTITION = 1;

    @Value("${swallow.web.kafka.applytopic.qa.replica}")
    private int QA_N_REPLICA = 1;

    @Value("${swallow.web.kafka.applytopic.qa.zkserver}")
    private String QA_ZKSERVER = "192.168.229.178:2181,192.168.229.162:2181,192.168.229.146:2181";

    private static final int N_DURABLE_FIRST = 3;

    private static final int N_EFFICIENCY_FIRST = 2;

    protected final Logger logger = LogManager.getLogger(getClass());

    @RequestMapping(value = "/api/topic/apply", method = RequestMethod.POST)
    @ResponseBody
    public Object applyTopic(@RequestBody TopicApplyDto topicApplyDto, HttpServletRequest request) {

        TopicApplyResource topicApplyResource = (TopicApplyResource) request.getAttribute(LogFilter.TOPIC_APPLY_ATTR);
        topicApplyResource.setTopic(topicApplyDto.getTopic());
        topicApplyResource.setTopicApplyDto(topicApplyDto);

        ValidatorFilterResult validatorFilterResult = new ValidatorFilterResult();
        ValidatorFilterChain validatorFilterChain = filterChainFactory.createValidatorFilterChain();

        validatorFilterChain.addFilter(switchValidatorFilter);
        validatorFilterChain.addFilter(nameValidatorFilter);
        validatorFilterChain.addFilter(quoteValidatorFilter);
        validatorFilterChain.addFilter(typeValidatorFilter);
        validatorFilterChain.addFilter(applicantValidatorFilter);
        validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);

        if (validatorFilterResult.getStatus() != 0) {
            return validatorFilterResult;
        }

        LionConfigureResult lionConfigureResult = new LionConfigureResult();
        ConfigureHandlerChain configureHandlerChain = handlerChainFactory.createConfigureHandlerChain();

        if (topicApplyDto.isKafkaType()) {
            configureHandlerChain.addHandler(kafkaServerHandler);
        } else {
            configureHandlerChain.addHandler(mongoServerHandler);
            configureHandlerChain.addHandler(quoteHandler);
        }

        configureHandlerChain.addHandler(consumerServerHandler);
        ResponseStatus status = configureHandlerChain.handle(topicApplyDto, lionConfigureResult);

        topicApplyResource.setLionConfigureResult(lionConfigureResult);

        if (!ResponseStatus.SUCCESS.equals(status)) {
            return status;
        }

        EmptyObject emptyObject = new EmptyObject();
        LionEditorEntity lionEditorEntity = new LionEditorEntity();
        String topic = topicApplyDto.getTopic().trim();
        boolean isTest = topicApplyDto.isTest();

        lionEditorEntity.setTopic(topic);
        lionEditorEntity.setTest(isTest);
        lionEditorEntity.setStorageServer(lionConfigureResult.getStorageServer());
        lionEditorEntity.setConsumerServer(lionConfigureResult.getConsumerServer());
        lionEditorEntity.setSize4SevenDay(lionConfigureResult.getSize4SevenDay());
        lionEditorEntity.setTopicType(lionConfigureResult.getTopicType());

        LionHandlerChain lionHandlerChain = handlerChainFactory.createLionHandlerChain();
        lionHandlerChain.addHandler(topicWhiteListLionHandler);
        lionHandlerChain.addHandler(consumerServerLionHandler);
        lionHandlerChain.addHandler(topicCfgLionHandler);
        status = lionHandlerChain.handle(lionEditorEntity, emptyObject);

        if (!ResponseStatus.SUCCESS.equals(status)) {
            return status;
        }

        String applicant = topicApplyDto.getApplicant();
        Set<String> administrator = new HashSet<String>();
        administrator.add(applicant.trim());

        boolean isSuccess;
        synchronized (APPLY_TOPIC) {
            isSuccess = topicResourceService.updateTopicAdministrator(topic, administrator);
            if(!isSuccess){
                return ResponseStatus.MONGOWRITE;
            }
            if (topicApplyDto.isKafkaType()) {
                String zkServer = lionEditorEntity.getStorageServer().substring(KafkaServerHandler.PRE_KAFKA.length());
                if(EnvUtil.isProduct()){
                    int N_REPLICA = lionEditorEntity.getTopicType().equals(TOPIC_TYPE.DURABLE_FIRST.toString()) ? N_DURABLE_FIRST : N_EFFICIENCY_FIRST;
                    isSuccess = kafkaService.createTopic(zkServer, topic, N_PARTITION, N_REPLICA);
                    if(!isSuccess){
                        kafkaService.cleanUpAfterCreateFail(zkServer, topic);
                        return ResponseStatus.KAFKACREATETOPIC;
                    }
                }

                isSuccess = kafkaService.createTopic(QA_ZKSERVER, topic, QA_N_PARTITION, QA_N_REPLICA);
                if(!isSuccess){
                    kafkaService.cleanUpAfterCreateFail(QA_ZKSERVER, topic);
                }
                return isSuccess ? ResponseStatus.SUCCESS : ResponseStatus.QAKAFKACREATETOPIC;
            }else{
                return ResponseStatus.SUCCESS;
            }

        }

    }

    @RequestMapping(value = "/console/topicapply")
    public ModelAndView topicApplyInfo() {

        return new ModelAndView("tool/topicapply", createViewMap());
    }


    @RequestMapping(value = "/console/topicapply/list", method = RequestMethod.POST)
    @ResponseBody
    public Object fetchTopicPage(@RequestBody TopicQueryDto topicQueryDto) {

        String topic = topicQueryDto.getTopic();
        int offset = topicQueryDto.getOffset();
        int limit = topicQueryDto.getLimit();

        if (StringUtils.isBlank(topic)) {
            return topicApplyService.findTopicApplyResourcePage(offset, limit);
        } else {
            return topicApplyService.find(topic, offset, limit);
        }

    }

    @RequestMapping(value = "/api/zk/clean", method = RequestMethod.GET)
    @ResponseBody
    public Object cleanUpKafkaPath(String zk, String topic) {

        if(StringUtils.isBlank(zk) || StringUtils.isBlank(topic)){
            return ResponseStatus.EMPTYARGU;
        }
        boolean result = kafkaService.cleanUpAfterCreateFail(zk, topic);
        return result ? ResponseStatus.SUCCESS : ResponseStatus.ZKCLEANUP;
    }

    @Override
    protected String getMenu() {
        return "tool";
    }

    @Override
    protected String getSide() {

        return "applytopic";
    }

    private String subSide = "applyquery";

    @Override
    public String getSubSide() {

        return subSide;
    }

}

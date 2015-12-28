package com.dianping.swallow.web.controller.handler.lion;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.util.http.HttpMethod;
import com.dianping.swallow.web.controller.handler.AbstractHandler;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author mingdongli
 *         15/10/23 下午5:06
 */
public abstract class AbstractLionHandler extends AbstractHandler<LionEditorEntity, EmptyObject>{

    @Autowired
    protected LionUtil lionUtil;

    @Resource(name = "topicResourceService")
    protected TopicResourceService topicResourceService;

    protected abstract ResponseStatus doHandlerHelper(LionEditorEntity lionEditorEntity, EmptyObject result);

    @Override
    public Object handle(LionEditorEntity lionEditorEntity, EmptyObject result) {

        ResponseStatus status = doHandlerHelper(lionEditorEntity, result);
        if (!ResponseStatus.SUCCESS.equals(status)) {
            String topic = lionEditorEntity.getTopic();
            topicResourceService.loadCachedTopicToAdministrator().remove(topic);
        }
        return status;
    }

    protected synchronized ResponseStatus doEditLion(String key, String newValue, String oldValue, boolean test, String env) {

        try {
            if(!test){
                lionUtil.createOrSetConfig(key, newValue, HttpMethod.POST, env);
            }
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Set value from \n[%s]\n to \n[%s]\n of lion key %s successfully", oldValue,
                        newValue, key));
            }
            return ResponseStatus.SUCCESS;
        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Set value from \n[%s]\n to \n[%s]\n of lion key %s failed", oldValue,
                        newValue, key));
            }
            return ResponseStatus.LIONEXCEPTION;
        }
    }

    protected Object getValue(String key, boolean split){

        String value = lionUtil.getValue(key);
        if(split){
            return splitString(value);
        }

        return value;
    }

    private Set<String> splitString(String value) {

        if (StringUtils.isBlank(value)) {
            return null;
        }

        List<String> topicList = com.dianping.swallow.common.internal.util.StringUtils.splitByDelimiter(value, Pattern.compile("\\s*;\\s*"));
        return new HashSet<String>(topicList);
    }

    public void setLionUtil(LionUtil lionUtil) {
        this.lionUtil = lionUtil;
    }

    public void setTopicResourceService(TopicResourceService topicResourceService) {
        this.topicResourceService = topicResourceService;
    }
}

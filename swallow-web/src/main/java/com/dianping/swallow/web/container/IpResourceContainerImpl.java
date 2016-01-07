package com.dianping.swallow.web.container;

import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.service.IpResourceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qi.yin
 *         2015/10/28  下午5:59.
 */
@Component
public class IpResourceContainerImpl extends AbstractContainer implements IpResourceContainer {

    private Map<String, String> ipResources = new ConcurrentHashMap<String, String>();

    @Autowired
    private IpResourceService ipResourceService;

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
        interval = 600;
        delay = 5;
        containerName = "IpResourceContainer";
    }

    @Override
    public String getApplicationName(String ip) {
        return ipResources.get(ip);
    }

    @Override
    public void doLoadResource() {
        logger.info("[doLoadResource] scheduled load alarmMeta info.");
        List<IpResource> ipResourcesDb = ipResourceService.findAll();
        if (ipResourcesDb != null && !ipResourcesDb.isEmpty()) {
            for (IpResource ipResource : ipResourcesDb) {
                if (StringUtils.isNotBlank(ipResource.getIp()) && StringUtils.isNotBlank(ipResource.getApplication())) {
                    ipResources.put(ipResource.getIp(), ipResource.getApplication());
                }
            }
        }
    }

}

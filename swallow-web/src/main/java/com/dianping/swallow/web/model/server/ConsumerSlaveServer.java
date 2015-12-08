package com.dianping.swallow.web.model.server;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.service.HttpService.HttpResult;

/**
 * @author qiyin
 *         <p/>
 *         2015年10月16日 下午3:41:24
 */
public class ConsumerSlaveServer extends ConsumerServer {

    private boolean isServiceLastAlarmed = false;

    private String slaveMonitorUrl = "http://{ip}:8080/names";

    public ConsumerSlaveServer(String ip, int port) {
        super(ip, port);
        isServiceLastAlarmed = false;
    }

    public void initServer() {
        super.initServer();
        slaveMonitorUrl = StringUtils.replace(serverConfig.getSlaveMonitorUrl(), "{ip}", ip);
    }

    public void checkService() {
        HttpResult result = requestUrl(slaveMonitorUrl);
        if (!result.isSuccess() || StringUtils.isBlank(result.getResponseBody())) {
            report(ip, ip, ServerType.SLAVE_SERVICE);
            isServiceLastAlarmed = true;
        } else {
            if (isServiceLastAlarmed) {
                report(ip, ip, ServerType.SLAVE_SERVICE_OK);
            }
            isServiceLastAlarmed = false;
        }
    }

    @Override
    public String toString() {
        return "ConsumerSlaveServer [isServiceLastAlarmed=" + isServiceLastAlarmed + "] " + super.toString();
    }

}

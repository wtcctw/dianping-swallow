package com.dianping.swallow.common.internal.util;

import com.dianping.lion.Constants;
import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月18日 下午4:54:27
 */
public class EnvUtil {

    private static final String basicWebAddress = "swallow.dp";

    private static final String protocal = "http://";

    private static final String env;

    private static final String swimeline;

    static {
        env = EnvZooKeeperConfig.getEnv().trim();
        swimeline = StringUtils.trimToNull(EnvZooKeeperConfig.getSwimlane());
    }

    public static String getEnv() {

        return env;
    }

    public static boolean isDev() {

        return env.equals("dev");
    }

    public static boolean isAlpha() {

        return env.equals("alpha");
    }

    public static boolean isQa() {

        return env.equals("qa");
    }

    public static boolean isPpe() {

        return env.equals("prelease");
    }

    public static boolean isProduct() {

        return env.equals("product");
    }

    public static boolean isSwimelineAlpha() {

        return "alpha".equals(swimeline);
    }

    public static Set<String> allEnv() {
        Set<String> envs = new HashSet<String>();
        envs.add("dev");
        envs.add("alpha");
        envs.add("qa");
        envs.add("prelease");
        envs.add("product");
        envs.add("performance");
        return envs;
    }

    public static String getWebAddress() {

        if (!isProduct()) {

            if (isQa()) {
                if (isSwimelineAlpha()) {
                    return protocal + "alpha." + basicWebAddress;
                } else {
                    return protocal + "beta." + basicWebAddress;
                }
            }

            if (isDev()) {
                return protocal + "localhost:8080";
            }

            if (isPpe()) {
                return protocal + "ppe." + basicWebAddress;
            }

            return protocal + env + "." + basicWebAddress;
        }

        return "http://" + basicWebAddress;
    }

    public static String getGroup() {

        String group = ConfigCache.getInstance().getAppenv(Constants.KEY_SWIMLANE);
        return StringUtils.trimToNull(group);

    }

    public static String getSwimeline() {

        return getGroup();
    }

}

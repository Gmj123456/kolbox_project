package org.jeecg.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

/**
 * @Author: nqr
 * @Date: 2025/9/13 08:59
 * @Description:
 **/
@Component
public class ProfileUtils {

    private static Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        ProfileUtils.environment = environment;
    }

    /**
     * 判断是否为dev环境
     * @return true-是dev环境，false-非dev环境
     */
    public static boolean isDev() {
        return environment != null && environment.acceptsProfiles(Profiles.of("dev"));
    }

    /**
     * 如果是dev环境就跳过执行
     * @return true-已跳过执行，false-继续执行
     */
    public static boolean skipIfDev() {
        if (isDev()) {
            System.out.println("dev环境跳过执行");
            return true;
        }
        return false;
    }

    /**
     * 判断是否为指定环境
     */
    public static boolean isProfile(String profile) {
        return environment != null && environment.acceptsProfiles(Profiles.of(profile));
    }
}
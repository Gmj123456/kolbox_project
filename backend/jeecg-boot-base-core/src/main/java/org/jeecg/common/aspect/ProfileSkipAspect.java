package org.jeecg.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jeecg.common.aspect.annotation.SkipOnProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

/**
 * @Author: nqr
 * @Date: 2025/9/13 09:03
 * @Description:
 **/
@Aspect
@Component
public class ProfileSkipAspect {

    @Autowired
    private Environment environment;

    @Around("@annotation(skipOnProfile)")
    public Object skipOnProfile(ProceedingJoinPoint joinPoint, SkipOnProfile skipOnProfile) throws Throwable {
        String[] profiles = skipOnProfile.value();

        for (String profile : profiles) {
            if (environment.acceptsProfiles(Profiles.of(profile))) {
                System.out.println(profile + "环境跳过执行: " + joinPoint.getSignature().getName());
                return null;
            }
        }

        return joinPoint.proceed();
    }
}
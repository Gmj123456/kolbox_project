package org.jeecg.common.aspect;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeecg.common.aspect.annotation.LimitSubmit;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
public class LimitSubmitAspect {
    /**
     * 封装了redis操作各种方法
     */
    @Autowired
    private RedisUtil redisUtil;

    @Pointcut("@annotation(org.jeecg.common.aspect.annotation.LimitSubmit)")
    private void pointcut() {
    }

   /* @Around("pointcut()")
    public Object handleSubmit(ProceedingJoinPoint joinPoint) throws Throwable {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取注解信息
        LimitSubmit limitSubmit = method.getAnnotation(LimitSubmit.class);
        int submitTimeLimiter = limitSubmit.limit();
        String redisKey = limitSubmit.key();
        boolean needAllWait = limitSubmit.needAllWait();
        String key = getRedisKey(sysUser, joinPoint, redisKey);
        Object result = redisUtil.get(key);
        if (result != null) {
            throw new JeecgBootException("请勿重复访问！");
        }
        redisUtil.set(key, sysUser.getId(), submitTimeLimiter);
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception in {}.{}() with cause = '{}' and exception = '{}'", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL", e.getMessage(), e);
            throw e;
        } finally {
            if (!needAllWait) {
                redisUtil.del(redisKey);
            }
        }
    }
*/
   @Around("pointcut()")
   public Object handleSubmit(ProceedingJoinPoint joinPoint) throws Throwable {
       LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
       Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
       // 获取注解信息
       LimitSubmit limitSubmit = method.getAnnotation(LimitSubmit.class);
       int submitTimeLimiter = limitSubmit.limit();
       String redisKey = limitSubmit.key();
       boolean needAllWait = limitSubmit.needAllWait();

       // 获取IP地址
       String ipAddress = getClientIpAddress();
       // 使用用户ID或IP地址生成Redis键
       String key = sysUser != null
               ? getRedisKey(sysUser, joinPoint, redisKey)
               : getRedisKeyByIp(ipAddress, joinPoint, redisKey);

       Object result = redisUtil.get(key);
       if (result != null) {
           throw new JeecgBootException("请勿重复访问！");
       }

       // 设置Redis值，用户存在时用用户ID，不存在时用IP
       String value = sysUser != null ? sysUser.getId() : ipAddress;
       redisUtil.set(key, value, submitTimeLimiter);

       try {
           return joinPoint.proceed();
       } catch (Throwable e) {
           log.error("在 {}.{}() 中发生异常，原因 = '{}'，异常信息 = '{}'",
                   joinPoint.getSignature().getDeclaringTypeName(),
                   joinPoint.getSignature().getName(),
                   e.getCause() != null ? e.getCause() : "NULL",
                   e.getMessage(), e);
           throw e;
       } finally {
           if (!needAllWait) {
               redisUtil.del(key);
           }
       }
   }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip != null ? ip : "unknown";
    }

    /**
     * 根据IP生成Redis键
     */
    private String getRedisKeyByIp(String ipAddress, ProceedingJoinPoint joinPoint, String redisKey) {
        StringBuilder key = new StringBuilder(redisKey);
        key.append(":ip:").append(ipAddress);
        return key.toString();
    }
    /**
     * 支持多参数，从请求参数进行处理
     */
    private String getRedisKey(LoginUser sysUser, ProceedingJoinPoint joinPoint, String key) {
        // 替换 %s 为用户 ID
        if (key.contains("%s")) {
            key = String.format(key, sysUser.getId());
        }

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();

        // 使用新的参数名发现器
        StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                String paramName = parameterNames[i];
                String placeholder = "#" + paramName;
                if (key.contains(placeholder)) {
                    Object argValue = args[i];
                    String argStr = (argValue != null) ? argValue.toString() : "";
                    key = key.replace(placeholder, argStr);
                }
            }
        }

        return key;
    }
}
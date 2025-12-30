package com.github.mykyta.sirobaba.ailearningtracker.aspects;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * Created by Mykyta Sirobaba on 31.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAspect {

    @Pointcut("execution(* com.github.mykyta.sirobaba.ailearningtracker.controllers..*(..))")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("==> REQUEST: [{} {}] -> Method: {}.{}",
                request.getMethod(), request.getRequestURI(), className, methodName);
        log.debug("==> ARGS: {}", Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed(args);

            long executionTime = System.currentTimeMillis() -  startTime;
            log.info("<== RESPONSE: Status: OK | Time: {}ms | Body: {}", executionTime, result);
            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("<== ERROR in {}.{} | Time: {}ms | Message: {}", className, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }
}

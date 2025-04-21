package com.klaxon.diary.config.log;

import com.klaxon.diary.config.log.hidden.Hidden;
import com.klaxon.diary.config.log.hidden.Sanitizer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.klaxon.diary.util.MdcKey.TRACE_ID;
import static com.klaxon.diary.util.MdcKey.USER_ID;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around(value = "@annotation(Log)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toShortString();
        Log logAnnotation = signature.getMethod().getAnnotation(Log.class);
        if (logAnnotation == null) {
            return joinPoint.proceed();
        }
        String traceId = Optional.ofNullable(MDC.get(TRACE_ID)).orElse("unknown");
        String userId = Optional.ofNullable(MDC.get(USER_ID)).orElse("anonymous");

        if (logAnnotation.logArgs()) {
            Object[] args = joinPoint.getArgs();
            Parameter[] parameters = signature.getMethod().getParameters();
            Map<String, Object> sanitizedArgs = new LinkedHashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                String argName = parameters[i].getName();
                Hidden hiddenAnnotation = parameters[i].getAnnotation(Hidden.class);
                if (hiddenAnnotation != null) {
                    sanitizedArgs.put(argName, hiddenAnnotation.mask());
                } else {
                    sanitizedArgs.put(argName, Sanitizer.toJson(args[i]));
                }
            }
            log.info("[traceId={}, userId={}] → {} with arguments: {}", traceId, userId, methodName, sanitizedArgs);
        } else {
            log.info("[traceId={}, userId={}] → {}", traceId, userId, methodName);
        }
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            traceId = Optional.ofNullable(MDC.get(TRACE_ID)).orElse("unknown");
            userId = Optional.ofNullable(MDC.get(USER_ID)).orElse("anonymous");
            if (logAnnotation.logResult()) {
                String resultStr = Sanitizer.toJson(result);
                log.info("[traceId={}, userId={}] ← {} with result: {}. Execution time: {} ms", traceId, userId, methodName, resultStr, executionTime);
            } else {
                log.info("[traceId={}, userId={}] ← {}. Execution time: {} ms", traceId, userId, methodName, executionTime);
            }
            return result;
        } catch (Throwable t) {
            long executionTime = System.currentTimeMillis() - start;
            traceId = Optional.ofNullable(MDC.get(TRACE_ID)).orElse("unknown");
            userId = Optional.ofNullable(MDC.get(USER_ID)).orElse("anonymous");
            if (logAnnotation.logError()) {
                log.error("[traceId={}, userId={}] ✖ {} threw an exception: {}. Message: {}. Execution time: {} ms", traceId, userId, methodName, t.getClass().getName(), t.getMessage(), executionTime);
            }
            throw t;
        }
    }
}

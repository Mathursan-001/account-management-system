package com.rhb.ams.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Aspect configuration for logging method execution and exception handling
 * Provides cross-cutting concerns for logging across the application
 */
@Slf4j
@Aspect
@Component
public class LoggingAspectConfig {

    /**
     * Pointcut for all controller methods
     */
    @Pointcut("execution(* com.rhb.ams.controller.*.*(..))")
    public void controllerMethods() {
    }

    /**
     * Pointcut for all service methods
     */
    @Pointcut("execution(* com.rhb.ams.service.*.*(..))")
    public void serviceMethods() {
    }

    /**
     * Pointcut for all repository methods
     */
    @Pointcut("execution(* com.rhb.ams.repository.*.*(..))")
    public void repositoryMethods() {
    }

    /**
     * Log before controller method execution
     */
    @Before("controllerMethods()")
    public void logBeforeController(JoinPoint joinPoint) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            log.info("Incoming Request: {} {} | Controller: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    joinPoint.getSignature().getName());
            if (log.isDebugEnabled()) {
                log.debug("Arguments: {}", joinPoint.getArgs());
            }
        }
    }

    /**
     * Log before service method execution
     */
    @Before("serviceMethods()")
    public void logBeforeService(JoinPoint joinPoint) {
        log.info("Service Method: {} ", joinPoint.getSignature().getName());
        if (log.isDebugEnabled()) {
            log.debug("Arguments: {}", joinPoint.getArgs());
        }
    }

    /**
     * Log after successful method execution
     */
    @After("controllerMethods() || serviceMethods()")
    public void logAfterMethodExecution(JoinPoint joinPoint) {
        log.info("Method: {} execution completed successfully", joinPoint.getSignature().getName());
    }

    /**
     * Log after returning from method
     */
    @AfterReturning(pointcut = "controllerMethods() || serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            log.info("Outgoing Response: {} {} | Status: SUCCESS | Body: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    result);
        }
    }

    /**
     * Log when an exception is thrown
     */
    @AfterThrowing(pointcut = "controllerMethods() || serviceMethods() || repositoryMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        log.error("Exception in method: {} with message: {}",
                joinPoint.getSignature().getName(),
                exception.getMessage(),
                exception);
    }
}

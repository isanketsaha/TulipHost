package com.tulip.host.aop.logging;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import tech.jhipster.config.JHipsterConstants;

/**
 * Aspect for logging execution of service and repository Spring components.
 * Optimized for performance - only logs errors and important events.
 */
@Aspect
public class LoggingAspect {

    private final Environment env;
    private static final long SLOW_METHOD_THRESHOLD_MS = 2000; // 2 seconds
    private static final long SLOW_QUERY_THRESHOLD_MS = 1000; // 1 second

    // Configurable thresholds with defaults
    @Value("${application.slow-method-threshold-ms:2000}")
    private long slowMethodThresholdMs;

    @Value("${application.slow-query-threshold-ms:1000}")
    private long slowQueryThresholdMs;

    public LoggingAspect(Environment env) {
        this.env = env;
    }

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut(
        "within(@org.springframework.stereotype.Repository *)" +
        " || within(@org.springframework.stereotype.Service *)" +
        " || within(@org.springframework.web.bind.annotation.RestController *)"
    )
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.tulip.host.repository..*)" + " || within(com.tulip.host.service..*)" + " || within(com.tulip.host.web.rest..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Retrieves the {@link Logger} associated to the given {@link JoinPoint}.
     *
     * @param joinPoint join point we want the logger for.
     * @return {@link Logger} associated to the given {@link JoinPoint}.
     */
    private Logger logger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * Advice that logs methods throwing exceptions.
     * Only logs exceptions - no method entry/exit logging for performance.
     *
     * @param joinPoint join point for advice.
     * @param e         exception.
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Logger log = logger(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        // Add minimal context information to MDC
        MDC.put("method", methodName);
        MDC.put("class", className);
        MDC.put("exception", e.getClass().getSimpleName());

        try {
            // Only log the exception message and cause, no sensitive data
            String causeMessage = e.getCause() != null ? e.getCause().getClass().getSimpleName() : "NULL";
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "NULL";

            // Truncate long messages to prevent log flooding
            if (exceptionMessage.length() > 200) {
                exceptionMessage = exceptionMessage.substring(0, 200) + "...";
            }

            log.error("Exception in {}.{}() - Cause: {} - Message: {}",
                    className,
                    methodName,
                    causeMessage,
                    exceptionMessage);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Advice that logs only slow methods and important events.
     * No verbose entry/exit logging for performance.
     *
     * @param joinPoint join point for advice.
     * @return result.
     * @throws Throwable throws {@link IllegalArgumentException}.
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = logger(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();

        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            // Only log slow methods (performance issue)
            if (duration > slowMethodThresholdMs) {
                log.warn("Slow method: {}.{}() took {}ms", className, methodName, duration);
            }

            return result;
        } catch (IllegalArgumentException e) {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            log.error("Illegal argument in {}.{}() (took {}ms)", className, methodName, duration);
            throw e;
        } catch (Exception e) {
            // Let the @AfterThrowing advice handle the detailed exception logging
            throw e;
        }
    }
}

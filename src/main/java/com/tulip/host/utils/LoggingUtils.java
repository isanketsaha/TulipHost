package com.tulip.host.utils;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for centralized logging with focus on important events and
 * errors only.
 * Optimized for performance - minimal logging overhead.
 */
@Component
public class LoggingUtils {

    private static final Logger log = LoggerFactory.getLogger(LoggingUtils.class);

    // Default thresholds that can be overridden via application properties
    private static final long DEFAULT_SLOW_METHOD_THRESHOLD_MS = 2000; // 2 seconds

    @Value("${application.slow-method-threshold-ms:2000}")
    private long slowMethodThresholdMs;

    @Value("${application.slow-query-threshold-ms:1000}")
    private long slowQueryThresholdMs;

    /**
     * Log critical exceptions with minimal context.
     * Only for important errors that need immediate attention.
     *
     * @param logger     the logger to use
     * @param methodName the method name
     * @param className  the class name
     * @param exception  the exception
     * @param context    additional context information (optional)
     */
    public static void logCriticalException(Logger logger, String methodName, String className, Throwable exception,
            Map<String, Object> context) {
        MDC.put("method", methodName);
        MDC.put("class", className);
        MDC.put("exception", exception.getClass().getSimpleName());
        MDC.put("event", "CRITICAL_ERROR");

        if (context != null) {
            context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
        }

        try {
            String causeMessage = exception.getCause() != null ? exception.getCause().getClass().getSimpleName()
                    : "NULL";
            String exceptionMessage = exception.getMessage() != null ? exception.getMessage() : "NULL";

            // Truncate long messages
            if (exceptionMessage.length() > 200) {
                exceptionMessage = exceptionMessage.substring(0, 200) + "...";
            }

            logger.error("Critical error in {}.{}() - Cause: {} - Message: {}",
                    className,
                    methodName,
                    causeMessage,
                    exceptionMessage);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Log security events (authentication, authorization failures).
     * Only important security events, not every login.
     *
     * @param logger    the logger to use
     * @param eventType the type of security event
     * @param username  the username (will be sanitized)
     * @param details   additional details
     */
    public static void logSecurityEvent(Logger logger, String eventType, String username, String details) {
        // Only log security failures and important events
        if (eventType.contains("FAILURE") || eventType.contains("VIOLATION") || eventType.contains("ATTEMPT")) {
            MDC.put("event", "SECURITY");
            MDC.put("security_event_type", eventType);
            MDC.put("username", sanitizeUsername(username));

            logger.warn("Security event: {} for user: {} - {}", eventType, sanitizeUsername(username), details);

            MDC.clear();
        }
    }

    /**
     * Log important business events only.
     * Not every CRUD operation, only significant business events.
     *
     * @param logger     the logger to use
     * @param eventType  the type of business event
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @param details    additional details
     */
    public static void logImportantBusinessEvent(Logger logger, String eventType, String entityType, String entityId,
            String details) {
        // Only log important business events
        if (isImportantBusinessEvent(eventType)) {
            MDC.put("event", "BUSINESS");
            MDC.put("business_event_type", eventType);
            MDC.put("entity_type", entityType);
            MDC.put("entity_id", entityId);

            logger.info("Important business event: {} - Entity: {} (ID: {}) - {}", eventType, entityType, entityId,
                    details);

            MDC.clear();
        }
    }

    /**
     * Log data access events for audit purposes.
     * Only for sensitive operations or compliance requirements.
     *
     * @param logger     the logger to use
     * @param operation  the database operation
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @param username   the username performing the operation
     */
    public static void logSensitiveDataAccess(Logger logger, String operation, String entityType, String entityId,
            String username) {
        // Only log sensitive data access
        if (isSensitiveEntity(entityType) || isSensitiveOperation(operation)) {
            MDC.put("event", "SENSITIVE_DATA_ACCESS");
            MDC.put("operation", operation);
            MDC.put("entity_type", entityType);
            MDC.put("entity_id", entityId);
            MDC.put("username", sanitizeUsername(username));

            logger.info("Sensitive data access: {} {} (ID: {}) by user: {}", operation, entityType, entityId,
                    sanitizeUsername(username));

            MDC.clear();
        }
    }

    /**
     * Log performance issues only.
     *
     * @param logger     the logger to use
     * @param methodName the method name
     * @param className  the class name
     * @param duration   the execution duration in milliseconds
     */
    public void logPerformanceIssue(Logger logger, String methodName, String className, long duration) {
        if (duration > slowMethodThresholdMs) {
            MDC.put("event", "PERFORMANCE");
            MDC.put("method", methodName);
            MDC.put("class", className);
            MDC.put("duration_ms", String.valueOf(duration));

            logger.warn("Performance issue: {}.{}() took {}ms", className, methodName, duration);

            MDC.clear();
        }
    }

    /**
     * Determine if a business event is important enough to log.
     */
    private static boolean isImportantBusinessEvent(String eventType) {
        return eventType.contains("CREATED") ||
                eventType.contains("DELETED") ||
                eventType.contains("APPROVED") ||
                eventType.contains("REJECTED") ||
                eventType.contains("PAYMENT") ||
                eventType.contains("ENROLLMENT") ||
                eventType.contains("GRADUATION");
    }

    /**
     * Determine if an entity type is sensitive.
     */
    private static boolean isSensitiveEntity(String entityType) {
        return entityType.contains("User") ||
                entityType.contains("Payment") ||
                entityType.contains("Financial") ||
                entityType.contains("Personal") ||
                entityType.contains("Medical") ||
                entityType.contains("Legal");
    }

    /**
     * Determine if an operation is sensitive.
     */
    private static boolean isSensitiveOperation(String operation) {
        return operation.equals("DELETE") ||
                operation.equals("UPDATE") ||
                operation.contains("PASSWORD") ||
                operation.contains("SECRET");
    }

    /**
     * Sanitize username for logging - only show first and last character.
     */
    private static String sanitizeUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "[ANONYMOUS]";
        }

        // For security, only show first and last character
        if (username.length() <= 2) {
            return username;
        }

        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
}
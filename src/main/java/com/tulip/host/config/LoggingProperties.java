package com.tulip.host.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for logging settings.
 */
@Component
@ConfigurationProperties(prefix = "application.logging")
public class LoggingProperties {

    private Performance performance = new Performance();
    private Security security = new Security();
    private Audit audit = new Audit();

    public Performance getPerformance() {
        return performance;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public static class Performance {
        private long slowQueryThresholdMs = 1000;
        private long slowMethodThresholdMs = 500;
        private boolean enablePerformanceLogging = true;

        public long getSlowQueryThresholdMs() {
            return slowQueryThresholdMs;
        }

        public void setSlowQueryThresholdMs(long slowQueryThresholdMs) {
            this.slowQueryThresholdMs = slowQueryThresholdMs;
        }

        public long getSlowMethodThresholdMs() {
            return slowMethodThresholdMs;
        }

        public void setSlowMethodThresholdMs(long slowMethodThresholdMs) {
            this.slowMethodThresholdMs = slowMethodThresholdMs;
        }

        public boolean isEnablePerformanceLogging() {
            return enablePerformanceLogging;
        }

        public void setEnablePerformanceLogging(boolean enablePerformanceLogging) {
            this.enablePerformanceLogging = enablePerformanceLogging;
        }
    }

    public static class Security {
        private boolean logAuthenticationEvents = true;
        private boolean logAuthorizationEvents = true;
        private boolean logSecurityExceptions = true;
        private boolean maskSensitiveData = true;

        public boolean isLogAuthenticationEvents() {
            return logAuthenticationEvents;
        }

        public void setLogAuthenticationEvents(boolean logAuthenticationEvents) {
            this.logAuthenticationEvents = logAuthenticationEvents;
        }

        public boolean isLogAuthorizationEvents() {
            return logAuthorizationEvents;
        }

        public void setLogAuthorizationEvents(boolean logAuthorizationEvents) {
            this.logAuthorizationEvents = logAuthorizationEvents;
        }

        public boolean isLogSecurityExceptions() {
            return logSecurityExceptions;
        }

        public void setLogSecurityExceptions(boolean logSecurityExceptions) {
            this.logSecurityExceptions = logSecurityExceptions;
        }

        public boolean isMaskSensitiveData() {
            return maskSensitiveData;
        }

        public void setMaskSensitiveData(boolean maskSensitiveData) {
            this.maskSensitiveData = maskSensitiveData;
        }
    }

    public static class Audit {
        private boolean logDataAccess = true;
        private boolean logBusinessEvents = true;
        private boolean logUserActions = true;
        private boolean logSystemEvents = true;

        public boolean isLogDataAccess() {
            return logDataAccess;
        }

        public void setLogDataAccess(boolean logDataAccess) {
            this.logDataAccess = logDataAccess;
        }

        public boolean isLogBusinessEvents() {
            return logBusinessEvents;
        }

        public void setLogBusinessEvents(boolean logBusinessEvents) {
            this.logBusinessEvents = logBusinessEvents;
        }

        public boolean isLogUserActions() {
            return logUserActions;
        }

        public void setLogUserActions(boolean logUserActions) {
            this.logUserActions = logUserActions;
        }

        public boolean isLogSystemEvents() {
            return logSystemEvents;
        }

        public void setLogSystemEvents(boolean logSystemEvents) {
            this.logSystemEvents = logSystemEvents;
        }
    }
}
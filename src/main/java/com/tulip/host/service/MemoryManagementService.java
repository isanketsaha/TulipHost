package com.tulip.host.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

@Service
@Slf4j
public class MemoryManagementService {

    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    /**
     * Log current memory usage
     */
    public void logMemoryUsage() {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        log.info("=== Memory Usage Report ===");
        log.info("Heap Memory:");
        log.info("  Used: {} MB", heapMemoryUsage.getUsed() / 1024 / 1024);
        log.info("  Committed: {} MB", heapMemoryUsage.getCommitted() / 1024 / 1024);
        log.info("  Max: {} MB", heapMemoryUsage.getMax() / 1024 / 1024);
        log.info("  Usage: {}%", (heapMemoryUsage.getUsed() * 100) / heapMemoryUsage.getMax());

        log.info("Non-Heap Memory:");
        log.info("  Used: {} MB", nonHeapMemoryUsage.getUsed() / 1024 / 1024);
        log.info("  Committed: {} MB", nonHeapMemoryUsage.getCommitted() / 1024 / 1024);
        log.info("  Max: {} MB", nonHeapMemoryUsage.getMax() / 1024 / 1024);

        log.info("Thread Count: {}", threadBean.getThreadCount());
        log.info("==========================");
    }

    /**
     * Check if memory usage is high
     */
    public boolean isMemoryUsageHigh() {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        double usagePercentage = (heapMemoryUsage.getUsed() * 100.0) / heapMemoryUsage.getMax();
        return usagePercentage > 80.0;
    }

    /**
     * Get memory usage percentage
     */
    public double getMemoryUsagePercentage() {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        return (heapMemoryUsage.getUsed() * 100.0) / heapMemoryUsage.getMax();
    }

    /**
     * Suggest garbage collection (but don't force it)
     */
    public void suggestGarbageCollection() {
        if (isMemoryUsageHigh()) {
            log.warn("Memory usage is high ({}%), suggesting garbage collection",
                    String.format("%.1f", getMemoryUsagePercentage()));
            System.gc(); // This is just a suggestion, JVM may ignore it
        }
    }

    /**
     * Get memory statistics as a string
     */
    public String getMemoryStats() {
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        double usagePercentage = getMemoryUsagePercentage();

        return String.format(
                "Heap: %dMB/%dMB (%.1f%%) | NonHeap: %dMB | Threads: %d",
                heapMemoryUsage.getUsed() / 1024 / 1024,
                heapMemoryUsage.getMax() / 1024 / 1024,
                usagePercentage,
                memoryBean.getNonHeapMemoryUsage().getUsed() / 1024 / 1024,
                threadBean.getThreadCount());
    }
}
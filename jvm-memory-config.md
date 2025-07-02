# JVM Memory Management Configuration

## Recommended JVM Options for Production

Add these JVM options to your application startup to optimize memory management:

```bash
# Memory Settings
-Xms2g                    # Initial heap size
-Xmx4g                    # Maximum heap size
-XX:MetaspaceSize=256m    # Initial metaspace size
-XX:MaxMetaspaceSize=512m # Maximum metaspace size

# Garbage Collection Settings
-XX:+UseG1GC             # Use G1 Garbage Collector
-XX:MaxGCPauseMillis=200 # Target max GC pause time
-XX:G1HeapRegionSize=16m # G1 region size
-XX:G1NewSizePercent=30  # Percentage of heap for young generation
-XX:G1MaxNewSizePercent=40

# Memory Leak Prevention
-XX:+HeapDumpOnOutOfMemoryError  # Create heap dump on OOM
-XX:HeapDumpPath=/tmp/heapdump.hprof
-XX:+PrintGCDetails       # Print GC details
-XX:+PrintGCTimeStamps    # Print GC timestamps
-XX:+PrintGCDateStamps    # Print GC date stamps

# Performance Optimizations
-XX:+UseStringDeduplication  # Deduplicate strings
-XX:+OptimizeStringConcat     # Optimize string concatenation
-XX:+UseCompressedOops        # Use compressed object pointers
-XX:+UseCompressedClassPointers

# Monitoring
-XX:+PrintTenuringDistribution
-XX:+PrintGCApplicationStoppedTime
```

## Application Properties for Memory Management

Add these to your `application.yml`:

```yaml
spring:
  jpa:
    properties:
      # Memory management optimizations
      hibernate.jdbc.batch_versioned_data: true
      hibernate.jdbc.batch.builder: legacy
      hibernate.connection.handling_mode: DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION
      hibernate.session.events.log: false
      hibernate.connection.release_mode: after_transaction
      hibernate.jdbc.use_get_generated_keys: true
      hibernate.jdbc.wrap_result_sets: false
      hibernate.jdbc.use_scrollable_resultset: false
      
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      leak-detection-threshold: 60000
```

## Memory Monitoring Commands

```bash
# Monitor JVM memory usage
jstat -gc <pid> 1000

# Monitor heap usage
jmap -histo <pid>

# Create heap dump
jmap -dump:format=b,file=heapdump.hprof <pid>

# Analyze heap dump
jhat heapdump.hprof
```

## Common Memory Issues and Solutions

1. **EntityManager not cleared**: Always use `em.clear()` in finally blocks
2. **Large collections**: Use pagination and limit result sets
3. **Stream operations**: Avoid collecting large streams into memory
4. **Transaction timeouts**: Set reasonable timeouts for long-running operations
5. **Connection leaks**: Use connection pooling with proper configuration 
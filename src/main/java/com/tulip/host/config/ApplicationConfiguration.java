package com.tulip.host.config;

import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public VelocityEngine velocityEngine() {
        Properties props = new Properties();
        // Velocity 2.x keys
        props.put("resource.loaders", "class");
        props.put("resource.loader.class.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        props.put("resource.default_encoding", "UTF-8");
        props.put("output.encoding", "UTF-8");
        props.put("runtime.strict_mode.enable", "true");

        VelocityEngine engine = new VelocityEngine(props);
        engine.init();
        return engine;
    }
}

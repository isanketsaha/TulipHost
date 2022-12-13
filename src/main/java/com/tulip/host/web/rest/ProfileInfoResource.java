package com.tulip.host.web.rest;

import com.tulip.host.config.DefaultProfileUtil;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.config.JHipsterProperties;

/**
 * Resource to return information about the currently running Spring profiles.
 */
@RestController
@RequestMapping("/api")
public class ProfileInfoResource {

    private final Environment env;

    private final JHipsterProperties jHipsterProperties;

    public ProfileInfoResource(Environment env, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.jHipsterProperties = jHipsterProperties;
    }

    @GetMapping("/profile-info")
    public ProfileInfoVM getActiveProfiles() {
        String[] activeProfiles = DefaultProfileUtil.getActiveProfiles(env);
        return new ProfileInfoVM(activeProfiles);
    }

    class ProfileInfoVM {

        private String[] activeProfiles;

        ProfileInfoVM(String[] activeProfiles) {
            this.activeProfiles = activeProfiles;
        }

        public String[] getActiveProfiles() {
            return activeProfiles;
        }
    }
}

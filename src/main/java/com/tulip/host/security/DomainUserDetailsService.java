package com.tulip.host.security;

import com.tulip.host.data.LoginDTO;
import com.tulip.host.repository.CredentialRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final PasswordEncoder passwordEncoder;

    private final CredentialRepository credentialRepository;

    @Override
    @Transactional(readOnly = false)
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        return credentialRepository
            .findByUserId(login)
            .map(user -> createSpringSecurityUser(login, user))
            .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, LoginDTO user) {
        if (!user.getActive()) {
            throw new UsernameNotFoundException("User " + lowercaseLogin + " is not active" + "");
        }
        return new org.springframework.security.core.userdetails.User(
            user.getName(),
            user.getPassword(), //Default Password - tulip123,
            !user.getLocked(),
            true,
            true,
            user.getActive(),
            Arrays.asList(new SimpleGrantedAuthority(user.getAuthority()))
        );
    }
}

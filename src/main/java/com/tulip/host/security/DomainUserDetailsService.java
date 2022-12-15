package com.tulip.host.security;

import com.tulip.host.data.pojo.LoginPojo;
import com.tulip.host.domain.Authority;
import com.tulip.host.repository.CredentialRepository;
import com.tulip.host.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    private final CredentialRepository credentialRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        return credentialRepository
            .findByUserId(login)
            .map(user -> createSpringSecurityUser(login, user))
            .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, LoginPojo user) {
        //        if (!user.getActive()) {
        //            throw new UsernameNotFoundException("User " + lowercaseLogin + " was not activated");
        //        }
        return new org.springframework.security.core.userdetails.User(
            user.getUserName(),
            user.getPassword(),
            Arrays.asList(new SimpleGrantedAuthority(user.getAuthority()))
        );
    }
}

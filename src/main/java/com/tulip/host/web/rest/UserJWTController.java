package com.tulip.host.web.rest;

import com.tulip.host.data.LoggedUserDTO;
import com.tulip.host.data.LoginDTO;
import com.tulip.host.repository.CredentialRepository;
import com.tulip.host.security.jwt.JWTFilter;
import com.tulip.host.security.jwt.TokenProvider;
import com.tulip.host.web.rest.vm.LoginVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to authenticate users.
 */
@RestController
public class UserJWTController {

    private final TokenProvider tokenProvider;

    private final CredentialRepository credentialRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserJWTController(
        TokenProvider tokenProvider,
        AuthenticationManagerBuilder authenticationManagerBuilder,
        CredentialRepository credentialRepository
    ) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.credentialRepository = credentialRepository;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<LoggedUserDTO> authorize(@Valid @RequestBody LoginVM loginVM, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, loginVM.getRememberMe() == null ? false : loginVM.getRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        List<String> authorities = authentication
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        LoginDTO byUserId = this.credentialRepository.findByUserId(loginVM.getUsername()).orElse(null);
        LoggedUserDTO build = LoggedUserDTO
            .builder()
            .idToken(jwt)
            .userId(loginVM.getUsername())
            .userName(authentication.getName())
            .authority(authorities.get(0))
            .resetCredential(byUserId != null && byUserId.getResetCredential() != null ? byUserId.getResetCredential() : false)
            .build();
        return new ResponseEntity<>(build, httpHeaders, HttpStatus.OK);
    }
}

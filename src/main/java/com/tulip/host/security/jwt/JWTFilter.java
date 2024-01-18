//package com.tulip.host.security.jwt;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Arrays;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.GenericFilterBean;
//
///**
// * Filters incoming requests and installs a Spring Security principal if a header corresponding to a valid user is
// * found.
// */
//public class JWTFilter extends GenericFilterBean {
//
//    public static final String AUTHORIZATION_HEADER = "Authorization";
//
//    private final TokenProvider tokenProvider;
//
//    public JWTFilter(TokenProvider tokenProvider) {
//        this.tokenProvider = tokenProvider;
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
//        throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        String jwt = resolveToken(httpServletRequest);
//        if (StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt)) {
//            Authentication authentication = this.tokenProvider.getAuthentication(jwt);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            filterChain.doFilter(servletRequest, servletResponse);
//        } else if (Arrays.asList("/api/authenticate", "/api/management/info").contains(((HttpServletRequest) servletRequest).getRequestURI().toString())) {
//            filterChain.doFilter(servletRequest, servletResponse);
//        } else {
//            logger.error("Unable to get JWT Token or JWT Token has expired");
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("anonymous", "anonymous", null);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//    }
//
//    private String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}

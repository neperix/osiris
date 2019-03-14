package com.neperix.osiris.security.jwt;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.neperix.osiris.security.AuthenticatedUser;
import com.neperix.osiris.security.SecurityService;

import lombok.extern.slf4j.Slf4j;

/**
 * Extracts the JWT token from request header and populates security context based on it.
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtFactory jwtFactory;

    @Autowired
    private SecurityService securityService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Optional<Token> authToken = jwtFactory.decode(request.getHeader("Authorization"));

        if (authToken.isPresent() && !securityService.getAuthenticatedUser().isPresent()) {
            String username = authToken
                    .map(Token::getUsername)
                    .orElseThrow(() -> new JwtException("Unable to decode token: " + authToken));
            AuthenticatedUser authUser = securityService.find(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            securityService.loadToContext(authUser);
        }

        filterChain.doFilter(request, response);
    }
}

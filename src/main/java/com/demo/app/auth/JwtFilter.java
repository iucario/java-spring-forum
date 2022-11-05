package com.demo.app.auth;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class JwtFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;

    public JwtFilter(final String secret) {
        this.jwtUtil = new JwtUtil(secret);
    }

    /**
     * Filter to check if the request has a valid JWT token
     * Add the user to the security context
     * Add claims to user details
     */
    @Override
    public void doFilter(final ServletRequest req,
                         final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
        final String token = getTokenString(req).orElse(null);
        if (token == null) {
            chain.doFilter(req, res);
            return;
        }
        try { // TODO: ignore invalid token if endpoint is public
            final Claims claims = jwtUtil.getAllClaimsFromToken(token);
            String sub = claims.get("sub", String.class);
            UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(sub)
                    .password("")
                    .authorities(Arrays.asList())
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(claims);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (final Exception e) {
            handleException(res, "Invalid token.");
            return;
        }

        chain.doFilter(req, res);
    }

    private Optional<String> getTokenString(final ServletRequest req) {
        final HttpServletRequest request = (HttpServletRequest) req;
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }
        return Optional.empty();
    }

    private void handleException(final ServletResponse res, String message) throws IOException {
        final HttpServletResponse response = (HttpServletResponse) res;
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}

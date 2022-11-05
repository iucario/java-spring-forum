package com.demo.app.auth;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(@Value("${jwt.secret}") final String secret) {
        this.jwtUtil = new JwtUtil(secret);
    }

    /**
     * Filter to check if the request has a valid JWT token
     * Add the user to the security context
     * Add claims to user details
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest req,
                                    final HttpServletResponse res,
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

    private Optional<String> getTokenString(final HttpServletRequest req) {
        final String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }
        return Optional.empty();
    }

    private void handleException(final HttpServletResponse res, String message) throws IOException {
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}

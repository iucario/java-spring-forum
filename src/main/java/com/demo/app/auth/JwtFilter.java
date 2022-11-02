package com.demo.app.auth;

import io.jsonwebtoken.Claims;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;

    public JwtFilter(final String secret) {
        this.jwtUtil = new JwtUtil(secret);
    }

    /**
     * Filter to check if the request has a valid JWT token
     * Convert token to "claims" field in the request
     */
    @Override
    public void doFilter(final ServletRequest req,
                         final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            handleException(res, "Missing or invalid Authorization header.");
            return;
        }

        final String token = authHeader.substring(7); // The part after "Bearer "

        try {
            final Claims claims = jwtUtil.getAllClaimsFromToken(token);
            request.setAttribute("claims", claims);
        } catch (final Exception e) {
            handleException(res, "Invalid token.");
            return;
        }

        chain.doFilter(req, res);
    }

    private void handleException(final ServletResponse res, String message) throws IOException {
        final HttpServletResponse response = (HttpServletResponse) res;
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}

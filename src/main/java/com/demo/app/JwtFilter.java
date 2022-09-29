package com.demo.app;


import com.demo.app.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Override
    public void doFilter(final ServletRequest req,
                         final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ServletException("Missing or invalid Authorization header.");
        }

        final String token = authHeader.substring(7); // The part after "Bearer "

        try {
            final Claims claims = jwtUtil.getAllClaimsFromToken(token);
            request.setAttribute("claims", claims);
        } catch (final SecurityException e) {
            throw new ServletException("Invalid token.");
        }

        chain.doFilter(req, res);
    }

}

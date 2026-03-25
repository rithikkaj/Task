package com.example.taskmanager.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String requestPath = req.getRequestURI();
        
        // Skip JWT filter for public endpoints
        if (requestPath.startsWith("/auth/") || 
            requestPath.startsWith("/swagger-ui") || 
            requestPath.startsWith("/v3/api-docs")) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    throw new IllegalArgumentException("Token is invalid or expired");
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setHeader("WWW-Authenticate", "Bearer realm=\"TaskManager\", error=\"invalid_token\", error_description=\"" + e.getMessage() + "\"");
                res.setContentType("application/json;charset=UTF-8");
                res.getWriter().write("{\"error\":\"Invalid or expired token\", \"message\":\"" + e.getMessage() + "\"}");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}

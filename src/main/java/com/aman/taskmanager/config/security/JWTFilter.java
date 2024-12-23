package com.aman.taskmanager.config.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aman.taskmanager.handler.GenericResponse;
import com.aman.taskmanager.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            // Authorization
            String userName = null;
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                token = authHeader.substring(7);
                userName = jwtService.extractUsername(token);
            }
            // validate token
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // do filter
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                Boolean validateToken = jwtService.validateToken(token, userDetails);

                if (validateToken) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

        } catch (Exception e) {
            sendErrorResponse(response, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, Exception errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Object error = GenericResponse.builder()
                .status(errorMessage.getMessage())
                .responseStatus(HttpStatus.UNAUTHORIZED)
                .build().create().getBody();
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }

}

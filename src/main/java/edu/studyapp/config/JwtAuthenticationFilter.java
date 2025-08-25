package edu.studyapp.config;

import edu.studyapp.service.factory.GradeRatingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNullApi;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtService jwtService) {

        this.jwtService = jwtService;
        }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        logger.trace("=== JWT FILTER STARTED ===");
        logger.debug("Request URI: {}", request.getRequestURI());

        try {
            String token = extractToken(request);
            if (token != null) {
                if (jwtService.validateToken(token)) {
                    String username = jwtService.getUsernameFromToken(token);
                    List<String> roles = jwtService.extractRolesFromToken(token);
                    logger.debug("Authenticating user: {} with roles: {}", username, roles);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username, null,
                            roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("Authentication set for user: {}", username);
                } else {
                    logger.warn("Invalid JWT token");
                }
            } else {
                logger.debug("Не найден JWT token в запросе");
            }
        } catch (Exception e) {
            logger.error("JWT Filter error: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
        logger.trace("JWT filter процесс завершен");
    }

        private String extractToken(HttpServletRequest request) {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                return header.substring(7);
            }
            return null;
        }

    @Override
    public String toString() {
        return "JwtAuthenticationFilter{" +
                "jwtService=" + jwtService +
                '}';
    }
}

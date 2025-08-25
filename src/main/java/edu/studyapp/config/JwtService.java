package edu.studyapp.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class JwtService {
    private static final String SECRET_KEY = "myverysecuresecretkey256bit12345678910111277";
    private static final byte[] KEY_BYTES = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
    private final long EXPIRATION_TIME = 900000; // 15 минут
    final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String generateToken(Authentication authentication) {
        logger.debug("Начало генерации токена для пользователя: {}", authentication.getName());
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .toList();
        logger.debug("Роли пользователя {}: {}", authentication.getName(), roles);
        try {
            String token = Jwts.builder()
                    .subject(authentication.getName())
                    .claim("roles", roles)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(Keys.hmacShaKeyFor(KEY_BYTES), Jwts.SIG.HS256)
                    .compact();
            logger.info("Токен успешно сгенерирован для пользователя: {}", authentication.getName());
            return token;
        } catch (JwtException e) {
            logger.error("Ошибка генерации JWT токена для пользователя {}: {}",
                    authentication.getName(), e.getMessage(), e);
            throw new RuntimeException("Ошибка генерации токена", e);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при генерации токена для пользователя {}: {}",
                    authentication.getName(), e.getMessage(), e);
            throw new RuntimeException("Неожиданная ошибка генерации токена", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            logger.debug("Валидация токена (длина: {})", token != null ? token.length() : 0);
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(KEY_BYTES))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            Date expiration = claims.getExpiration();

            logger.debug("Токен валиден для: {}, истекает: {}", subject, expiration);

            return true;
        } catch (ExpiredJwtException ex) {
            logger.error("Токен истек: {}", ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            logger.error("Неверный формат токен: {}", ex.getMessage());
            return false;
        } catch (Exception ex) {
            logger.error("Ошибка валидации токена: {}", ex.getMessage());
            return false;
        }

    }

    public String getUsernameFromToken(String token) {
        logger.debug("Извлечение username из токена");
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(KEY_BYTES))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    public List<String> extractRolesFromToken(String token) {
        logger.debug("Извлечение ролей из токена");
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(KEY_BYTES))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        List<String> roles = claims.get("roles", List.class);
        logger.debug("Извлечено ролей: {}", roles.size());
        logger.trace("Список ролей: {}", roles);
        return roles;
    }

    public long getEXPIRATION_TIME() {
        return EXPIRATION_TIME;
    }

    @Override
    public String toString() {
        return "JwtService{" +
                "EXPIRATION_TIME=" + EXPIRATION_TIME +
                '}';
    }
}
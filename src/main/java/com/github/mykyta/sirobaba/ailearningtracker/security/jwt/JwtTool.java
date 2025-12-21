package com.github.mykyta.sirobaba.ailearningtracker.security.jwt;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.Invalid2FaTokenException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Created by Mykyta Sirobaba on 19.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Component
public class JwtTool {
    private final String jwtSecret;
    private final Long jwtExpiration;
    private final Long refreshExpiration;
    private final Long jwt2FaToken;

    public JwtTool(@Value("${tokenKey}") String jwtSecret,
                   @Value("${accessTokenValidTimeInMillisecond}") Long jwtExpiration,
                   @Value("${refreshTokenValidTimeInMillisecond}") Long refreshExpiration,
                   @Value("${jwt2FaToken}")Long jwt2FaToken) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
        this.jwt2FaToken = jwt2FaToken;
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("id", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate(jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("refreshKey", user.getRefreshTokenKey())
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate(refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token, UserDetails user) {
        String email = extractEmail(token);
        return email.equals(user.getUsername()) && isValid(token);
    }

    public boolean validateRefreshToken(String token, User user) {
        String email = extractEmail(token);
        String refreshKey = extractClaim(token, "refreshKey", String.class);
        return email.equals(user.getEmail())
               && refreshKey.equals(user.getRefreshTokenKey())
               && !isValid(token);
    }

    public String generate2FaToken(User user) {
        Date expiration = new Date(System.currentTimeMillis() + jwt2FaToken);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("authStage", "2fa_pending")
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public Long getUserIdFrom2FaToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        if (!"2fa_pending".equals(claims.get("authStage"))) {
            throw new Invalid2FaTokenException(ErrorMessage.INVALID_TOKEN_TYPE);
        }

        return claims.get("userId", Long.class);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        return extractAllClaims(token).getExpiration().after(new Date());

    }

    public <T> T extractClaim(String token, String claim, Class<T> type) {
        return extractAllClaims(token).get(claim, type);
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public long getExpirationDate(String token) {
        return extractAllClaims(token).getExpiration().getTime();
    }
    private Date getExpirationDate(long validity) {
        return new Date(System.currentTimeMillis() + validity);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

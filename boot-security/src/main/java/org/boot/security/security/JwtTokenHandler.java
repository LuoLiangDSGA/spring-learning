package org.boot.security.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.boot.security.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author luoliang
 * @date 2018/7/10
 */
@Component
@Slf4j
public class JwtTokenHandler {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expire}")
    private Long expire;

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean validateToken(String token, UserDetails user) {
        try {
            String username = getUsernameByToken(token);

            return username.equals(user.getUsername())
                    && !isTokenExpired(token);
        } catch (JwtException e) {
            throw new BusinessException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);

        return expiredDate.before(new Date());
    }

    public String refreshToken(UserDetails userDetails) {

        return generateToken(userDetails);
    }

    public String getUsernameByToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Date getCreatedDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Date created = new Date((Long) claims.get("created"));

        return created;
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expire * 1000);
    }

    private Date getExpiredDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            log.error(e.getMessage(), e);
        }
        return claims;
    }
}

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

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            throw new BusinessException("Expired or invalid JWT token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getUsernameByToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expire * 1000);
    }

}

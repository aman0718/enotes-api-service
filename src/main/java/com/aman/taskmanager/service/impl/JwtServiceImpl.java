package com.aman.taskmanager.service.impl;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.aman.taskmanager.entity.User;
import com.aman.taskmanager.exception.JwtTokenExpiredException;
import com.aman.taskmanager.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    private String secretKey = "";

    public JwtServiceImpl() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("role", user.getRoles());
        claims.put("status", user.getAccountStatus().getIsActive());

        String token = Jwts.builder()
                .claims().add(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 10))
                .and()
                .signWith(getKey())
                .compact();

        return token;
    }

    private Key getKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public String role(String token) {
        Claims claims = extractAllClaims(token);
        String role = claims.get("role").toString();
        return role;
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(decryptKey(secretKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException("Token is expired");
        } catch (JwtException e) {
            throw new JwtTokenExpiredException("Token is invalid");
        } catch (Exception e) {
            throw e;
        }
    }

    private SecretKey decryptKey(String secretKey) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {

        String userName = extractUsername(token);
        Boolean isExpired = isTokenExpired(token);

        if (userName.equalsIgnoreCase(userDetails.getUsername()) && !isExpired) {
            return true;
        }
        return false;
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

}

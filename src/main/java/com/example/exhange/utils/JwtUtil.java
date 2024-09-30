/* (C)2024 */
package com.example.exchange.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

  @Value("${password.hash.secret}")
  private String SECRET_KEY;

  private Key getSigningKey() {
    byte[] keyBytes = SECRET_KEY.getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String email) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, email);
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      // The token is invalid or has expired
      return false;
    }
  }
}

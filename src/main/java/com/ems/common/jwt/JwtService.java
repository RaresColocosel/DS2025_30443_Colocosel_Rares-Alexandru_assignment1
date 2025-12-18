package com.ems.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

  private final Key signingKey;
  private final String issuer;
  private final long expirationMinutes;

  public JwtService(
          @Value("${APP_JWT_SECRET:ZkA3PzM3s9vC1xQ8rT5bN2yL7wR0kE6uHfG2pQ9sV4yT1mC8aR6dW3qL9zX5tB2}") String secret,
          @Value("${APP_JWT_ISSUER:ems-auth}") String issuer,
          @Value("${APP_JWT_EXPIRATION_MINUTES:120}") long expirationMinutes) {

    // secret must be at least 32 bytes for HS256
    this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = issuer;
    this.expirationMinutes = expirationMinutes;
  }

  public String generateToken(String username, String role) {
    Instant now = Instant.now();
    Instant exp = now.plus(expirationMinutes, ChronoUnit.MINUTES);

    return Jwts.builder()
            .setSubject(username)
            .setIssuer(issuer)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(exp))
            .claim("role", role)
            // with 0.12.x you can just pass the key; algorithm is inferred
            .signWith(signingKey)
            .compact();
  }

  public String extractUsername(String token) {
    return parseClaims(token).getSubject();
  }

  public String extractRole(String token) {
    Object value = parseClaims(token).get("role");
    return value != null ? value.toString() : null;
  }

  public boolean isTokenValid(String token, String expectedUsername) {
    try {
      Claims claims = parseClaims(token);
      String subject = claims.getSubject();
      Date exp = claims.getExpiration();
      return subject != null
              && subject.equals(expectedUsername)
              && exp.after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    // JJWT 0.12.x style parsing
    return Jwts.parser()
            .verifyWith((SecretKey) signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }
}

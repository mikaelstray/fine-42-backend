package com.mikael.project.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JWTUtil {

  private static final Duration CLOCK_SKEW = Duration.ofSeconds(5);

  @Value("${security.jwt.secret-key}")
  private String secretKeyBase64;

  @Value("${security.jwt.access-token-expiration}")
  private long accessTokenExpirationMillis;

  @Getter
  private Key signingKey;

  @PostConstruct
  public void init() {
    byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKeyBase64);
    this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    log.debug("JWT signing key initialized");
  }

  public String generateToken(UserDetails userDetails) {
    Instant now = Instant.now();
    Date issuedAt   = Date.from(now);
    Date expiration = Date.from(now.plusMillis(accessTokenExpirationMillis));

    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList());

    log.debug("Generating JWT for user {} with claims {}", userDetails.getUsername(), claims);

    return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = extractAllClaims(token);
      Instant expiration = claims.getExpiration().toInstant();
      boolean valid = Instant.now().minus(CLOCK_SKEW).isBefore(expiration);
      if (!valid) {
        log.debug("JWT expired at {}", expiration);
      }
      return valid;
    } catch (JwtException | IllegalArgumentException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
      return false;
    }
  }
}

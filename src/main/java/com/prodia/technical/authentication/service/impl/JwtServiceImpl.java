package com.prodia.technical.authentication.service.impl;

import com.prodia.technical.authentication.persistence.entity.Jwt;
import com.prodia.technical.authentication.persistence.repository.JwtRepository;
import com.prodia.technical.authentication.security.SecurityConstant;
import com.prodia.technical.authentication.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
  private String signingKey = "AY4B42SH34MC5XL9UR64AVRM4JR9QZK33VXFZ6DUE02UML1319AWBSYN7423NWJN";
  private final String password = "TzMUocMF4p";
  @Setter(onMethod_ = {@Autowired}, onParam_ = {@Lazy})
  private JwtRepository jwtRepository;

  @Override
  public String extractUserName(String token) {
    return extractClaim(token, claims -> claims.get(SecurityConstant.USER_NAME, String.class));
  }

  @Override
  public String extractSubject(String token) {
    return extractClaim(token, Claims::getSubject);
  }


  public String extractCompanyId(String token) {
    return extractClaim(token, claims -> claims.get("companyId", String.class));
  }


  @Override
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  @Override
  public String generateToken(UserDetails userDetails, String userAgent) {
    HashMap<String, Object> claims = new HashMap<String, Object>();
    claims.put(SecurityConstant.USER_NAME, userDetails.getUsername());
    claims.put(SecurityConstant.USER_AGENT, userAgent);
    return generateToken(claims);
  }

  private String generateToken(Map<String, Object> extraClaims) {
    return Jwts.builder().setClaims(extraClaims).setSubject("access/" + password)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1440))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String userName = extractUserName(token);
    return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  @Override
  @Transactional
  public void addWhiteList(Jwt jwt) {
    jwtRepository.saveAndFlush(jwt);
  }

  @Override
  public void validateToken(String token, String userAgent) throws Exception {
    if (!extractSubject(token).contains("access")) {
      throw new Exception("Token subject wrong");
    }
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
    final Claims claims = extractAllClaims(token);
    return claimsResolvers.apply(claims);
  }

  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 1440))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }


  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(signingKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  @Transactional
  public void deleteByAccessToken(String accessToken) {
    jwtRepository.deleteByAccessToken(accessToken);
  }

  @Override
  public boolean isWhitelistExist(String token, UserDetails userDetails) {
    return jwtRepository.existsByAccessTokenAndUserUsername(token, userDetails.getUsername())
        && isTokenValid(token, userDetails);
  }
}

package com.prodia.technical.authentication.service;

import com.prodia.technical.authentication.persistence.entity.Jwt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JwtService {
  
  String extractUserName(String token);

  String extractSubject(String token);

  String generateToken(UserDetails userDetails);
  String generateToken(UserDetails userDetails, String userAgent);

  boolean isTokenValid(String token, UserDetails userDetails);

  void addWhiteList(Jwt jwt);

  void validateToken(String token, String userAgent) throws Exception;
  
  void deleteByAccessToken(String userId);
  
  boolean isWhitelistExist(String token, UserDetails userDetails);
  
}
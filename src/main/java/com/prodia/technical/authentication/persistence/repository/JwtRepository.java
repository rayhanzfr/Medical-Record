package com.prodia.technical.authentication.persistence.repository;

import com.prodia.technical.authentication.persistence.entity.Jwt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, String>, JpaSpecificationExecutor<Jwt> {
  Jwt findByAccessToken(String token);
  
  boolean existsByAccessTokenAndUserUsername(String accessToken, String userId);
  
  long deleteByAccessToken(String accessToken);
  
}
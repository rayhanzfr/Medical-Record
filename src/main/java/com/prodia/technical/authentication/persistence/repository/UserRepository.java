package com.prodia.technical.authentication.persistence.repository;

import com.prodia.technical.authentication.persistence.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String>, JpaSpecificationExecutor<User> {

  boolean existsByUsername(String username);
  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email,String id);
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
}

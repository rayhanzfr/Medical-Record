package com.prodia.technical.authentication.persistence.entity;

import com.prodia.technical.persistence.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "utl_jwt_whitelists", uniqueConstraints = {@UniqueConstraint(columnNames = {"access_token"})})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class Jwt extends AuditableEntity {
  @Column(name = "access_token")
  private String accessToken;

  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

}
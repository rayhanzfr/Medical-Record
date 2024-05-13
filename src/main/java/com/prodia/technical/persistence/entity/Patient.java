package com.prodia.technical.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "pro_patients",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name","email"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE pro_patients SET deleted_at = now() WHERE id=? AND version =?")
@Where(clause = "deleted_at IS NULL")
public class Patient extends MasterEntity{

  @Column(name = "name")
  private String name;
  @Column(name = "birth_date")
  private LocalDate birthDate;
  @Column(name = "email")
  private String email;
  @Column(name = "phone")
  private String phone;
}

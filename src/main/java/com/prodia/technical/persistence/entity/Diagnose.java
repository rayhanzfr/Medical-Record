package com.prodia.technical.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "pro_diagnoses",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"medical_status_id","code"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE pro_diagnoses SET deleted_at = now() WHERE id=? AND version =?")
@Where(clause = "deleted_at IS NULL")
public class Diagnose extends MasterEntity{

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "medical_record_id")
  private MedicalRecord medicalRecord;

  @Column(name = "code")
  private String code;

  @Column(name = "description")
  private String description;
}

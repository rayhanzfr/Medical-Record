package com.prodia.technical.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class MasterEntity extends DeleteableEntity {

  @Column(name = "is_active")
  private Boolean isActive;

}
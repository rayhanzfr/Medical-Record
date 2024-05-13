package com.prodia.technical.persistence.entity;

import com.prodia.technical.logging.listener.AuditTrailListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, AuditTrailListener.class})
@Getter
@Setter
public abstract class AuditableEntity {

  @Id
  @Column(name = "id", nullable = false)
  @UuidGenerator
  private String id;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private ZonedDateTime createdAt;

  @Column(name = "created_by", nullable = false, updatable = false)
  @CreatedBy
  private String createdBy;

  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  private ZonedDateTime updatedAt;

  @Column(name = "updated_by", nullable = false)
  @LastModifiedBy
  private String updatedBy;

  @Column(name = "version")
  @Version
  private Long version;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy
        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
        : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy
        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
        : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    AuditableEntity that = (AuditableEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy hibernateProxy
        ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }

  @Override
  public String toString() {
    return "id='" + id + '\'' + ", createdAt=" + createdAt.toInstant().toEpochMilli()
        + ", createdBy='" + createdBy + '\'' + ", updatedAt=" + updatedAt.toInstant()
        .toEpochMilli() + ", updatedBy='" + updatedBy + '\'' + ", version=" + version;
  }

}
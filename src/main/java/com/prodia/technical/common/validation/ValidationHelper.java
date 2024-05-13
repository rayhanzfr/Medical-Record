package com.prodia.technical.common.validation;

import com.prodia.technical.common.helper.error.ConstraintValidationException;
import com.prodia.technical.common.helper.validation.PersistenceValidationHelper;
import com.prodia.technical.persistence.entity.AuditableEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidationHelper {

  private static final String KEY_REASON = "reason";
  private final Validator validator;
  private final PersistenceValidationHelper persistenceValidationHelper;

  public void validate(Object object) {
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);
    if (!constraintViolations.isEmpty()) {
      throw new ConstraintViolationException(constraintViolations);
    }
  }
  public <T> void validateValue(Class<T> beanType, String propertyName, Object value) {
    Set<ConstraintViolation<T>> constraintViolations =
        validator.validateValue(beanType, propertyName, value);
    if (!constraintViolations.isEmpty()) {
      throw new ConstraintViolationException(constraintViolations);
    }
  }

  public <T> Set<ConstraintViolation<T>> getConstraintViolation(T object) {
    return validator.validate(object);
  }

  public ConstraintValidationException addPrefixProperties(String prefix, Integer index,
      ConstraintValidationException ex) {
    if (ex.getErrors() == null) {
      return new ConstraintValidationException(ex.getConstraintViolations(), ex.getErrors());
    }
    Map<String, List<String>> newErrors = new HashMap<>();
    for (var entry : ex.getErrors().entrySet()) {
      if (!entry.getKey().equals(KEY_REASON)) {
        newErrors.put(prefix + "[" + index + "]." + entry.getKey(), entry.getValue());
      } else {
        newErrors.put(entry.getKey(), entry.getValue());
      }
    }
    return new ConstraintValidationException(ex.getConstraintViolations(), newErrors);
  }

  public ConstraintValidationException addPrefixProperties(String prefix, Integer index,
      ConstraintViolationException ex) {
    Map<String, List<String>> errors =
        ValidationErrorMapper.getErrorsFrom(ex.getConstraintViolations());
    Map<String, List<String>> newErrors = new HashMap<>();
    for (var entry : errors.entrySet()) {
      if (!entry.getKey().equals(KEY_REASON)) {
        newErrors.put(prefix + "[" + index + "]." + entry.getKey(), entry.getValue());
      } else {
        newErrors.put(entry.getKey(), entry.getValue());
      }
    }
    return new ConstraintValidationException(null, newErrors);
  }

  public ConstraintValidationException addPrefixProperties(String prefix,
      ConstraintValidationException ex) {
    if (ex.getErrors() == null) {
      return new ConstraintValidationException(ex.getConstraintViolations(), ex.getErrors());
    }
    Map<String, List<String>> newErrors = new HashMap<>();
    for (var entry : ex.getErrors().entrySet()) {
      if (!entry.getKey().equals(KEY_REASON)) {
        newErrors.put(prefix + "." + entry.getKey(), entry.getValue());
      } else {
        newErrors.put(entry.getKey(), entry.getValue());
      }
    }
    return new ConstraintValidationException(ex.getConstraintViolations(), newErrors);
  }

  public ConstraintValidationException addPrefixProperties(String prefix,
      ConstraintViolationException ex) {
    Map<String, List<String>> errors =
        ValidationErrorMapper.getErrorsFrom(ex.getConstraintViolations());
    Map<String, List<String>> newErrors = new HashMap<>();
    for (var entry : errors.entrySet()) {
      if (!entry.getKey().equals(KEY_REASON)) {
        newErrors.put(prefix + "." + entry.getKey(), entry.getValue());
      } else {
        newErrors.put(entry.getKey(), entry.getValue());
      }
    }
    return new ConstraintValidationException(null, newErrors);
  }

  public boolean isUsedAsFk(Class<?> entityClass, String id) {
    return persistenceValidationHelper.isUsedAsFk(entityClass, id, null);
  }

  public boolean isUsedAsFk(Class<?> entityClass, String id, List<Class<?>> excludedEntityClasses) {
    return persistenceValidationHelper.isUsedAsFk(entityClass, id, excludedEntityClasses);
  }

  public void validateVersion(AuditableEntity entity, Long version) {
    if (!entity.getVersion().equals(version)) {
      throw new ConstraintValidationException("version", "is not match");
    }
  }
}

package com.prodia.technical.common.helper.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class ConstraintValidationException extends ValidationException {

  private final transient Set<ConstraintViolation<?>> constraintViolations;
  private final Map<String, List<String>> errors;

  public ConstraintValidationException(Set<? extends ConstraintViolation<?>> constraintViolations,
      Map<String, List<String>> errors) {
    this.constraintViolations = Collections.unmodifiableSet(
        Objects.requireNonNullElseGet(constraintViolations, Collections::emptySet));
    this.errors = Objects.requireNonNullElseGet(errors, HashMap::new);
  }

  public ConstraintValidationException(String key, String message) {
    this.constraintViolations = Collections.emptySet();
    this.errors = Map.ofEntries(Map.entry(key, Collections.singletonList(message)));
  }
  
  @Override
  public String getMessage() {
    var constrainsMessage = constraintViolations.stream()
        .map(cv -> cv == null ? "null" : cv.getPropertyPath() + ": " + cv.getMessage())
        .collect(Collectors.joining(", "));
    var errorMessage = errors.entrySet()
        .stream()
        .map(entry -> entry.getKey() + ": " + Arrays.toString(entry.getValue().toArray()))
        .collect(Collectors.joining(", "));
    return constrainsMessage + ", " + errorMessage;
  }
}

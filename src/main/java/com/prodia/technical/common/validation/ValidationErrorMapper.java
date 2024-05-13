package com.prodia.technical.common.validation;

import com.prodia.technical.common.annotation.MetaData;
import com.prodia.technical.common.annotation.MetaDatas;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Slf4j
public class ValidationErrorMapper {

  private ValidationErrorMapper() {
    throw new IllegalStateException("Helper class can't be initialized");
  }

  public static Map<String, List<String>> getErrorsFrom(BindingResult result,
      MessageSource messageSource) {
    return getErrorsFrom(result, messageSource, Locale.getDefault());
  }

  public static Map<String, List<String>> getErrorsFrom(BindingResult result,
      MessageSource messageSource,
      Locale locale) {
    if (result.hasFieldErrors()) {
      Map<String, List<String>> map = new HashMap<>();

      for (FieldError fieldError : result.getFieldErrors()) {
        String field = fieldError.getField();

        if (!map.containsKey(fieldError.getField())) {
          map.put(field, new ArrayList<>());
        }

        String errorMessage = messageSource.getMessage(Objects.requireNonNull(fieldError.getCode()),
            fieldError.getArguments(),
            fieldError.getDefaultMessage(), locale);
        map.get(field).add(errorMessage);
      }

      return map;
    } else {
      return Collections.emptyMap();
    }
  }

  public static Map<String, List<String>> getErrorsFrom(
      Set<ConstraintViolation<?>> constraintViolations) {
    Map<String, List<String>> map = new HashMap<>();

    constraintViolations.forEach(violation -> {
      for (String attribute : getAttributes(violation)) {
        putEntry(map, attribute, violation.getMessage());
      }
    });

    return map;
  }

  public static void putEntry(Map<String, List<String>> map, String key, String value) {
    map.computeIfAbsent(key, s -> new ArrayList<>());
    map.get(key).add(value);
  }

  public static String[] getAttributes(ConstraintViolation<?> constraintViolation) {
    String[] values = (String[]) constraintViolation.getConstraintDescriptor().getAttributes()
        .get("path");
    if (values == null || values.length == 0) {
      return getAttributesFromPath(constraintViolation);
    } else {
      return values;
    }
  }

  public static String[] getAttributesFromPath(ConstraintViolation<?> constraintViolation) {
    return new String[]{constraintViolation.getPropertyPath().toString()};
  }

  public static Map<String, Map<String, String>> getMetaData(
      Set<ConstraintViolation<?>> constraintViolations) {
    Map<String, Map<String, String>> metadata = new HashMap<>();
    constraintViolations.forEach(violation -> {
      try {
        Class<?> beanClass = violation.getLeafBean().getClass();

        String field = "";
        for (Path.Node node : violation.getPropertyPath()) {
          field = node.getName();
        }

        var metaDatasOpt = Optional.ofNullable(ReflectionUtils.findField(beanClass, field))
          .map(declaredField -> declaredField.getAnnotation(MetaDatas.class));
        if (metaDatasOpt.isPresent()) {
          MetaDatas metaDatas = metaDatasOpt.get();
          Map<String, String> values = new HashMap<>();

          for (MetaData metaData : metaDatas.value()) {
            values.put(metaData.key(), metaData.value());
          }

          for (String attribute : getAttributes(violation)) {
            metadata.put(attribute, values);
          }
        }

      } catch (Exception throwable) {
        log.warn(throwable.getMessage(), throwable);
      }
    });

    return metadata;
  }

}

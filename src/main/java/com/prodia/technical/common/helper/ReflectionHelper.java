package com.prodia.technical.common.helper;

import com.prodia.technical.persistence.entity.AuditableEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JoinColumnOrFormula;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ReflectionHelper {

  private static final String UNKNOWN_VALUE = "unknown";

  public static String getModuleNameFromPackage(String packageName, String appName) {
    var splitStr = packageName.split("\\.");
    var splitAppName = Arrays.stream(appName.split("-")).skip(1).collect(Collectors.joining());
    var isGroupIdShown = false;
    var result = UNKNOWN_VALUE;
    for (String s : splitStr) {
      if (isGroupIdShown) {
        result = s;
        break;
      }
      if (splitAppName.contains(s)) {
        isGroupIdShown = true;
      }
    }
    return result;
  }

  public static String getTableNameFromEntityClass(Class<?> cls) {
    return Optional.ofNullable(cls.getAnnotation(Table.class))
      .map(Table::name)
      .orElse(UNKNOWN_VALUE);
  }

  public static List<Field> getAllFields(Class<?> cls, List<Field> fields) {
    if (cls != null && !Object.class.equals(cls)) {
      fields.addAll(Arrays.asList(cls.getDeclaredFields()));

      if (cls.getSuperclass() != null) {
        getAllFields(cls.getSuperclass(), fields);
      }

      return fields;
    } else {
      return Collections.emptyList();
    }
  }

  public static Map<String, Object> getChunkedProperties(List<Field> fields, Object object) {
    Map<String, Object> properties = new HashMap<>();
    for (Field field : fields) {
      Object fieldValue;
      try {
        field.setAccessible(true);
        fieldValue = field.get(object);
        field.setAccessible(false);
      } catch (IllegalAccessException e) {
        log.error("Error when get value from Field : " + field.getName(), e);
        fieldValue = UNKNOWN_VALUE;
      }
      if(field.getDeclaredAnnotation(JoinColumnOrFormula.class) != null) {
        continue;
      }
      final var finalFieldValue = fieldValue;
      Optional.ofNullable(field.getDeclaredAnnotation(JoinColumn.class))
        .ifPresentOrElse(joinColumn -> {
          if(field.getDeclaredAnnotation(OneToMany.class) == null) {
            properties.put(
                joinColumn.name(), 
                finalFieldValue instanceof AuditableEntity auditableEntity ? auditableEntity.getId() : finalFieldValue);
          }         
        }, () -> {
          properties.put(field.getName(), finalFieldValue);
        });
    }
    return properties;
  }

}

package com.prodia.technical.common.helper;


import com.prodia.technical.common.annotation.MessageProperty;
import com.prodia.technical.common.helper.error.ConstraintValidationException;
import com.prodia.technical.common.validation.ValidationErrorMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageHelper {

  @SneakyThrows
  public static ConstraintValidationException transform(Class<?> clazz, ConstraintValidationException constraintViolationException) {
    Map<String, List<String>> errors = constraintViolationException.getErrors();
    List<String> messages = new ArrayList<>();
    for(Entry<String, List<String>> entry : errors.entrySet()) {
      Field field;
      try {
        field = clazz.getDeclaredField(entry.getKey());
      }catch (NoSuchFieldException e) {
        field = clazz.getSuperclass().getDeclaredField(entry.getKey());
      }
      MessageProperty messageProperty = field.getDeclaredAnnotation(MessageProperty.class);
      for(String value : entry.getValue()) {
        messages.add(messageProperty != null ? messageProperty.messageProperty()+" "+value : value);
      }
    }
    return new ConstraintValidationException(null, Map.ofEntries(Map.entry("reason", messages)));
  }
  
  public static ResponseStatusException getMessage(Exception e) {
    if(e instanceof ResponseStatusException ex) {
      return ex;
      
    }else if(e instanceof ConstraintValidationException ex) {
      Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
      Map<String, List<String>> errors = ex.getErrors();
      String messages = !constraintViolations.isEmpty() ? (constraintViolations)+", "+getStringMessages(errors) 
      : getStringMessages(errors);
      return new ResponseStatusException(HttpStatus.BAD_REQUEST, messages);
      
    }else if(e instanceof ConstraintViolationException ex) {
      Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
      return new ResponseStatusException(HttpStatus.BAD_REQUEST, getStringMessages(constraintViolations));
      
    }
    return new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
  }
  
  public static String getStringMessages(Map<String, List<String>> errors) {
    StringBuilder messages = new StringBuilder();
    for(Entry<String, List<String>> entry : errors.entrySet()) {
      if(messages.isEmpty()) {
        messages.append(splitCamelCase(entry.getKey()))
        .append(" : ")
        .append(String.join(",", entry.getValue()));
        
      }else {
        messages.append(messages.toString())
        .append(". ")
        .append(splitCamelCase(entry.getKey()))
        .append(" : ")
        .append(String.join(",", entry.getValue()));
      }
    }
    return messages.toString();
  }
  
  public static String getStringMessages(Set<ConstraintViolation<?>> constraintViolations) {
    Map<String, List<String>> errors = ValidationErrorMapper.getErrorsFrom(constraintViolations);
    return getStringMessages(errors);
  }
  
  private static String splitCamelCase(String string) {
    return string.replaceAll(
       String.format("%s|%s|%s",
          "(?<=[A-Z])(?=[A-Z][a-z])",
          "(?<=[^A-Z])(?=[A-Z])",
          "(?<=[A-Za-z])(?=[^A-Za-z])"
       ),
       " "
    ).toLowerCase().replace("id", "");
  }
}
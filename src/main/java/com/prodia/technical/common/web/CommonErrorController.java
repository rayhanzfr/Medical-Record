package com.prodia.technical.common.web;

import com.jcraft.jsch.SftpException;
import com.prodia.technical.common.helper.ResponseHelper;
import com.prodia.technical.common.helper.error.ConstraintValidationException;
import com.prodia.technical.common.model.response.WebResponse;
import com.prodia.technical.common.validation.ValidationErrorMapper;
import jakarta.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@RestControllerAdvice("com.prodia")
@Slf4j
public class CommonErrorController implements MessageSourceAware {

  private MessageSource messageSource;

  @Override
  public void setMessageSource(@NonNull MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public WebResponse<Object> httpMessageNotReadableException(HttpMessageNotReadableException e) {
    log.warn(HttpMessageNotReadableException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST.name()).build();
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  public WebResponse<Object> throwable(Throwable e) {
    log.error(e.getClass().getName(), e);
    return WebResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name()).build();
  }

  @ExceptionHandler(ServerWebInputException.class)
  public ResponseEntity<WebResponse<Object>> serverWebInputException(ServerWebInputException e) {
    log.warn(ServerWebInputException.class.getName(), e);

    Map<String, List<String>> errors = new HashMap<>();
    if (e.getMethodParameter() != null) {
      errors.put(e.getMethodParameter().getParameterName(),
          Collections.singletonList(e.getReason()));
    }

    var httpStatus = HttpStatus.valueOf(e.getStatusCode().value());
    var response = WebResponse.builder().code(httpStatus.value()).status(httpStatus.name())
        .errors(errors).build();
    return ResponseEntity.status(httpStatus.value()).body(response);
  }

  @ExceptionHandler(value = {WebExchangeBindException.class, MethodArgumentNotValidException.class})
  public ResponseEntity<WebResponse<Object>> webExchangeBindException(WebExchangeBindException e) {
    log.warn(WebExchangeBindException.class.getName(), e);

    var httpStatus = HttpStatus.valueOf(e.getStatusCode().value());
    var response = WebResponse.builder().code(httpStatus.value()).status(httpStatus.name())
        .errors(ValidationErrorMapper.getErrorsFrom(e.getBindingResult(), messageSource)).build();
    return ResponseEntity.status(e.getStatusCode()).body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<WebResponse<Object>> constraintViolationException(
      ConstraintViolationException e) {
    log.warn(ConstraintViolationException.class.getName(), e);

    var response = WebResponse.builder().code(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST.name())
        .errors(ValidationErrorMapper.getErrorsFrom(e.getConstraintViolations()))
        .metadata(Collections.singletonMap("errors",
            ValidationErrorMapper.getMetaData(e.getConstraintViolations())))
        .build();
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ConstraintValidationException.class)
  public ResponseEntity<WebResponse<Object>> constraintValidationException(
      ConstraintValidationException e) {
    log.warn(e.getClass().getName(), e);
    var errors = new HashMap<>(e.getErrors());
    errors.putAll(ValidationErrorMapper.getErrorsFrom(e.getConstraintViolations()));
    return ResponseEntity.badRequest().body(ResponseHelper.badRequest(errors));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public <E extends ResponseStatusException> ResponseEntity<WebResponse<Object>> responseStatusException(
      E e) {
    log.warn(ResponseStatusException.class.getName(), e);
    Map<String, List<String>> errors = new HashMap<>();
    errors.put("reason", Collections.singletonList(e.getReason()));

    var httpStatus = HttpStatus.valueOf(e.getStatusCode().value());
    var response = WebResponse.builder().code(httpStatus.value()).status(httpStatus.name())
        .errors(errors).build();
    return ResponseEntity.status(e.getStatusCode()).body(response);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(NotAcceptableStatusException.class)
  public WebResponse<Object> notAcceptableStatusException(NotAcceptableStatusException e) {
    log.warn(NotAcceptableStatusException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.NOT_ACCEPTABLE.value())
        .status(HttpStatus.NOT_ACCEPTABLE.name()).build();
  }

  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
  public WebResponse<Object> unsupportedMediaTypeStatusException(
      UnsupportedMediaTypeStatusException e) {
    log.warn(UnsupportedMediaTypeStatusException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name()).build();
  }

  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ExceptionHandler(
      value = {MethodNotAllowedException.class, HttpRequestMethodNotSupportedException.class})
  public WebResponse<Object> methodNotAllowedException(MethodNotAllowedException e) {
    log.warn(MethodNotAllowedException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.METHOD_NOT_ALLOWED.value())
        .status(HttpStatus.METHOD_NOT_ALLOWED.name()).build();
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ServerErrorException.class)
  public WebResponse<Object> serverErrorException(ServerErrorException e) {
    log.warn(ServerErrorException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name()).build();
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(FileAlreadyExistsException.class)
  public WebResponse<Object> fileAlreadyExistException(FileAlreadyExistsException e) {
    log.warn(FileAlreadyExistsException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST.name()).build();
  }
  
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(AccessDeniedException.class)
  public WebResponse<Object> accessDeniedException(AccessDeniedException e) {
    log.warn(AccessDeniedException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name()).build();
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(NoSuchFileException.class)
  public WebResponse<Object> noSuchFileException(NoSuchFileException e) {
    log.warn(NoSuchFileException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST.name()).build();
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(FileNotFoundException.class)
  public WebResponse<Object> fileNotFoundException(FileNotFoundException e) {
    log.warn(FileNotFoundException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST.name()).build();
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(DirectoryNotEmptyException.class)
  public WebResponse<Object> directoryNotEmptyException(DirectoryNotEmptyException e) {
    log.warn(DirectoryNotEmptyException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST.name()).build();
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MalformedURLException.class)
  public WebResponse<Object> malformedUrlException(MalformedURLException e) {
    log.warn(MalformedURLException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST.name()).build();
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidPathException.class)
  public WebResponse<Object> invalidPathException(InvalidPathException e) {
    log.warn(InvalidPathException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST.name()).build();
  }
  
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(SftpException.class)
  public WebResponse<Object> sftpException(SftpException e) {
    log.warn(SftpException.class.getName(), e);
    return WebResponse.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.name()).build();
  }
  
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<WebResponse<Object>> missingServletRequestParameterException(MissingServletRequestParameterException e) {
    log.warn(MissingServletRequestParameterException.class.getName(), e);
    var errors = Map.ofEntries(Map.entry(e.getParameterName(), Collections.singletonList("is required")));
    return ResponseEntity.badRequest().body(ResponseHelper.badRequest(errors));
  }

}
package com.prodia.technical.common.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodia.technical.common.annotation.MetaData;
import com.prodia.technical.common.annotation.MetaDatas;
import com.prodia.technical.common.helper.error.ConstraintValidationException;
import com.prodia.technical.common.web.controller.CommonErrorControllerTest.Application.HelloRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@SpringBootTest(
  classes = CommonErrorControllerTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Slf4j
class CommonErrorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testThrowable() throws Exception {
    mockMvc.perform(get("/Throwable"))
      .andExpect(status().isInternalServerError())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testConstraintViolationException() throws Exception {
    mockMvc.perform(
        post("/ConstraintViolationException")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(objectMapper.writeValueAsString(HelloRequest.builder().build()))
      ).andExpect(status().isBadRequest())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testWebExchangeBindException() throws Exception {
    mockMvc.perform(
        post("/WebExchangeBindException")
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(objectMapper.writeValueAsString(HelloRequest.builder().build()))
      ).andExpect(status().isBadRequest())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testHttpMessageNotReadableException() throws Exception {
    mockMvc.perform(get("/HttpMessageNotReadableException"))
      .andExpect(status().isBadRequest())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testServerWebInputException() throws Exception {
    mockMvc.perform(get("/ServerWebInputException"))
      .andExpect(status().isBadRequest())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testResponseStatusException() throws Exception {
    mockMvc.perform(get("/ResponseStatusException"))
      .andExpectAll(
        status().isUnauthorized(),
        jsonPath("$.errors.reason").value("Ups")
      )
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testMediaTypeNotSupportedStatusException() throws Exception {
    mockMvc.perform(get("/MediaTypeNotSupportedStatusException"))
      .andExpect(status().isUnsupportedMediaType())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testNotAcceptableStatusException() throws Exception {
    mockMvc.perform(get("/NotAcceptableStatusException"))
      .andExpect(status().isNotAcceptable())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testUnsupportedMediaTypeStatusException() throws Exception {
    mockMvc.perform(get("/UnsupportedMediaTypeStatusException"))
      .andExpect(status().isUnsupportedMediaType())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testMethodNotAllowedException() throws Exception {
    mockMvc.perform(post("/MethodNotAllowedException"))
      .andExpect(status().isMethodNotAllowed())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testServerErrorException() throws Exception {
    mockMvc.perform(get("/ServerErrorException"))
      .andExpect(status().isInternalServerError())
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testMetadataException() throws Exception {
    mockMvc.perform(get("/MetadataException"))
      .andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errors.name").value("NotBlank"),
        jsonPath("$.errors.age").value("NotNull"),
        jsonPath("$.errors['nested.name']").value("NotBlank"),
        jsonPath("$.errors['nested.age']").value("NotNull"),
        jsonPath("$.metadata.errors['name'].message").value("NotBlank"),
        jsonPath("$.metadata.errors['age'].message").value("NotNull"),
        jsonPath("$.metadata.errors['nested.name'].message").value("NotBlank"),
        jsonPath("$.metadata.errors['nested.age'].message").value("NotNull")
      )
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testConstraintValidationException() throws Exception {
    mockMvc.perform(get("/ConstraintValidationException"))
        .andExpectAll(
            status().isBadRequest(),
            jsonPath("errors.name").value("NotBlank"),
            jsonPath("errors.age").value("NotNull"),
            jsonPath("errors['nested.name']").value("NotBlank"),
            jsonPath("errors['nested.age']").value("NotNull"),
            jsonPath("errors.nestedError").value("Nested error")
        ).andDo(result -> log.info("[RESPONSE: {}]", result.getResponse().getContentAsString()));
  }

  @SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
  public static class Application {

    @RestController
    public static class ExampleController {

      @Autowired
      private ExampleService exampleService;

      @Autowired
      private Validator validator;

      @GetMapping("/Throwable")
      public String throwable() throws Throwable {
        throw new Throwable("Internal Server Error");
      }

      @PostMapping(value = "/ConstraintViolationException", consumes = MediaType.APPLICATION_JSON_VALUE)
      public String constraintViolationException(@RequestBody HelloRequest request) {
        exampleService.validated(request);
        return "OK";
      }

      @PostMapping(value = "/WebExchangeBindException", consumes = MediaType.APPLICATION_JSON_VALUE)
      public String webExchangeBindException(@RequestBody @Validated HelloRequest ignoredRequest) {
        return "OK";
      }

      @GetMapping("/HttpMessageNotReadableException")
      public String httpMessageNotReadableException() {
        throw new HttpMessageNotReadableException("HttpMessageNotReadable",
            new MockHttpInputMessage(InputStream.nullInputStream()));
      }

      @GetMapping("/ServerWebInputException")
      public String serverWebInputException() throws NoSuchMethodException {
        MethodParameter methodParameter = MethodParameter.forParameter(
          ExampleService.class.getMethod("validated", HelloRequest.class).getParameters()[0]);
        methodParameter.initParameterNameDiscovery(new StandardReflectionParameterNameDiscoverer());
        throw new ServerWebInputException("Ups", methodParameter);
      }

      @GetMapping("/ResponseStatusException")
      public String responseStatusException() {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ups");
      }

      @GetMapping(value = "/MediaTypeNotSupportedStatusException")
      public String mediaTypeNotSupportedStatusException() {
        throw new UnsupportedMediaTypeStatusException("Ups");
      }

      @GetMapping(value = "/NotAcceptableStatusException")
      public String notAcceptableStatusException() {
        throw new NotAcceptableStatusException("Ups");
      }

      @GetMapping("/UnsupportedMediaTypeStatusException")
      public String unsupportedMediaTypeStatusException() {
        throw new UnsupportedMediaTypeStatusException("Ups");
      }

      @GetMapping("/MethodNotAllowedException")
      public String methodNotAllowedException() {
        return "OK";
      }

      @GetMapping("/ServerErrorException")
      public String serverErrorException() {
        throw new ServerErrorException("Ups", new NullPointerException());
      }

      @GetMapping("/MetadataException")
      public String metadataValidation() {
        SampleRequest sampleRequest = SampleRequest.builder()
          .name("")
          .age(null)
          .nested(NestedSampleRequest.builder()
            .name("")
            .age(null)
            .build())
          .build();

        Set<ConstraintViolation<SampleRequest>> constraintViolations = validator.validate(sampleRequest);
        throw new ConstraintViolationException(constraintViolations);
      }

      @GetMapping("/ConstraintValidationException")
      public String constraintValidationException() {
        SampleRequest sampleRequest = SampleRequest.builder()
            .name("")
            .age(null)
            .nested(NestedSampleRequest.builder()
                .name("")
                .age(null)
                .build())
            .build();
        var constraintViolations = validator.validate(sampleRequest);
        throw new ConstraintValidationException(constraintViolations,
            Collections.singletonMap("nestedError", Collections.singletonList("Nested error")));
      }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SampleRequest {

      @MetaDatas(
        @MetaData(key = "message", value = "NotBlank")
      )
      @NotBlank(message = "NotBlank")
      private String name;

      @MetaDatas(
        @MetaData(key = "message", value = "NotNull")
      )
      @NotNull(message = "NotNull")
      private Integer age;

      @Valid
      private NestedSampleRequest nested;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NestedSampleRequest {

      @MetaDatas(
        @MetaData(key = "message", value = "NotBlank")
      )
      @NotBlank(message = "NotBlank")
      private String name;

      @MetaDatas(
        @MetaData(key = "message", value = "NotNull")
      )
      @NotNull(message = "NotNull")
      private Integer age;

    }

    @Service
    @Validated
    public static class ExampleService {

      @Validated
      public void validated(@Valid HelloRequest ignoredRequest) {

      }

    }

    /*@Slf4j
    @RestControllerAdvice
    public static class ErrorController extends CommonErrorController implements
        MessageSourceAware {

      public ErrorController(ValidationHelper validationHelper) {
        super(validationHelper);
      }

    }*/

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HelloRequest {

      @NotBlank
      private String name;

    }

  }
}

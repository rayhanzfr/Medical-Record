package com.prodia.technical.common.model;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prodia.technical.common.helper.ResponseHelper;
import com.prodia.technical.common.model.request.SortBy;
import com.prodia.technical.common.model.request.SortByDirection;
import com.prodia.technical.common.model.response.PagingResponse;
import com.prodia.technical.common.model.response.WebResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(
    classes = WebResponseTest.Application.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class WebResponseTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testPaging() throws Exception {
    mockMvc.perform(get("/paging"))
        .andExpectAll(
            status().isOk(),
            jsonPath("code").value(HttpStatus.OK.value()),
            jsonPath("status").value(HttpStatus.OK.name()),
            jsonPath("data[*]").value(contains("Foo", "Bar", "Alice")),
            jsonPath("paging.page").value(1),
            jsonPath("paging.pageSize").value(3),
            jsonPath("paging.totalItem").value(30),
            jsonPath("paging.totalPage").value(10),
            jsonPath("paging.sortBy[0].propertyName").value("first_name"),
            jsonPath("paging.sortBy[0].direction").value("ASC")
        );
  }

  @Test
  void testHello() throws Exception {
    mockMvc.perform(
        get(UriComponentsBuilder.fromPath("/hello").queryParam("name", "Foo").build().toUri())
    ).andExpectAll(
        status().isOk(),
        jsonPath("code").value(HttpStatus.OK.value()),
        jsonPath("status").value(HttpStatus.OK.name()),
        jsonPath("data.dummy").value("Hello Foo")
    );
  }

  @Test
  void testBadRequest() throws Exception {
    mockMvc.perform(get("/bad-request"))
        .andExpectAll(
            jsonPath("code").value(HttpStatus.BAD_REQUEST.value()),
            jsonPath("status").value(HttpStatus.BAD_REQUEST.name()),
            jsonPath("errors.firstName").value(contains("NotBlank", "NotNull"))
        );
  }

  @Test
  void testInternalServerError() throws Exception {
    mockMvc.perform(get("/internal-server-error"))
        .andExpectAll(
            jsonPath("code").value(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            jsonPath("status").value(HttpStatus.INTERNAL_SERVER_ERROR.name())
        );
  }

  @Test
  void testOk() throws Exception {
    mockMvc.perform(get("/ok"))
        .andExpectAll(
            jsonPath("code").value(HttpStatus.OK.value()),
            jsonPath("status").value(HttpStatus.OK.name())
        );
  }

  @Test
  void testUnauthorized() throws Exception {
    mockMvc.perform(get("/unauthorized"))
        .andExpectAll(
            jsonPath("code").value(HttpStatus.UNAUTHORIZED.value()),
            jsonPath("status").value(HttpStatus.UNAUTHORIZED.name())
        );
  }

  @Test
  void testRedirect() throws Exception {
    mockMvc.perform(get("/redirect"))
        .andExpectAll(
            jsonPath("code").value(HttpStatus.PERMANENT_REDIRECT.value()),
            jsonPath("status").value(HttpStatus.PERMANENT_REDIRECT.name())
        );
  }

  @SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
  public static class Application {

    @RestController
    public static class ExampleController {

      @GetMapping(value = "/paging", produces = MediaType.APPLICATION_JSON_VALUE)
      public ResponseEntity<WebResponse<List<String>>> paging() {
        return ResponseEntity.ok(
            ResponseHelper.ok(
                Arrays.asList("Foo", "Bar", "Alice"),
                PagingResponse.builder()
                    .page(1)
                    .pageSize(3)
                    .totalItem(30L)
                    .totalPage(10)
                    .sortBy(Collections.singletonList(
                        SortBy.builder()
                            .propertyName("first_name")
                            .direction(SortByDirection.ASC)
                            .build()
                    ))
                    .build()
            )
        );
      }

      @GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
      public ResponseEntity<WebResponse<DummyResponse>> response(
          @RequestParam("name") String name) {
        return ResponseEntity.ok(
            ResponseHelper.ok(new DummyResponse(String.format("Hello %s", name))));
      }

      @GetMapping("/bad-request")
      public ResponseEntity<WebResponse<String>> badRequest() {
        return ResponseEntity.badRequest()
            .body(ResponseHelper.badRequest(
                Collections.singletonMap("firstName", Arrays.asList("NotBlank", "NotNull"))
            ));
      }

      @GetMapping("/internal-server-error")
      public ResponseEntity<WebResponse<String>> internalServerError() {
        return ResponseEntity.internalServerError()
            .body(ResponseHelper.internalServerError());
      }

      @GetMapping("/ok")
      public ResponseEntity<WebResponse<String>> ok() {
        return ResponseEntity.ok(ResponseHelper.ok());
      }

      @GetMapping("/unauthorized")
      public ResponseEntity<WebResponse<String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ResponseHelper.unauthorized());
      }

      @GetMapping("/redirect")
      public ResponseEntity<WebResponse<String>> redirect() {
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
            .body(ResponseHelper.status(HttpStatus.PERMANENT_REDIRECT));
      }

    }

    @Data
    @AllArgsConstructor
    public static class DummyResponse {

      private String dummy;
    }

  }

}

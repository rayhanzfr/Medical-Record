package com.prodia.technical.common.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prodia.technical.common.CommonAutoConfiguration;
import com.prodia.technical.common.helper.PagingHelper;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.model.response.PagingResponse;
import com.prodia.technical.common.properties.PagingProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


@SpringBootTest(
  classes = PagingRequestArgumentResolverTest.Application.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Slf4j
class PagingRequestArgumentResolverTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PagingProperties pagingProperties;

  @Test
  void testPagingRequest() throws Exception {
    mockMvc.perform(get(
      UriComponentsBuilder.fromPath("/paging-request")
        .queryParam(pagingProperties.getQuery().getPageKey(), 1)
        .queryParam(pagingProperties.getQuery().getPageSizeKey(), 100)
        .queryParam(pagingProperties.getQuery().getSortByKey(),
          "firstName:asc,lastName:desc")
        .build()
        .toUri()
    )).andExpectAll(
      jsonPath("page").value(0),
      jsonPath("pageSize").value(100),
      jsonPath("sortBy[0].propertyName").value("firstName"),
      jsonPath("sortBy[0].direction").value("ASC"),
      jsonPath("sortBy[1].propertyName").value("lastName"),
      jsonPath("sortBy[1].direction").value("DESC")
    ).andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testDefaultPagingRequest() throws Exception {
    mockMvc.perform(get("/paging-request"))
      .andExpectAll(
        jsonPath("page").value(pagingProperties.getDefaultPage() - 1),
        jsonPath("pageSize").value(pagingProperties.getDefaultPageSize()),
        jsonPath("sortBy").isEmpty()
      ).andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testPaging() throws Exception {
    mockMvc.perform(get(
      UriComponentsBuilder.fromPath("/paging")
        .queryParam(pagingProperties.getQuery().getPageKey(), 1)
        .queryParam(pagingProperties.getQuery().getPageSizeKey(), 100)
        .queryParam(pagingProperties.getQuery().getSortByKey(), "firstName:asc,lastName:desc")
        .build()
        .toUri()
    )).andExpectAll(
      jsonPath("page").value(1),
      jsonPath("pageSize").value(100),
      jsonPath("totalPage").value(100),
      jsonPath("totalItem").value(100 * 100),
      jsonPath("sortBy[0].propertyName").value("firstName"),
      jsonPath("sortBy[0].direction").value("ASC"),
      jsonPath("sortBy[1].propertyName").value("lastName"),
      jsonPath("sortBy[1].direction").value("DESC")
    ).andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testDefaultPaging() throws Exception {
    mockMvc.perform(get("/paging"))
      .andExpectAll(
        jsonPath("page").value(pagingProperties.getDefaultPage()),
        jsonPath("pageSize").value(pagingProperties.getDefaultPageSize()),
        jsonPath("totalPage").value(100),
        jsonPath("totalItem").value(100 * pagingProperties.getDefaultPageSize()),
        jsonPath("sortBy").isEmpty()
      )
      .andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testUnexpectedSortByDirectionFromPagingRequest() throws Exception {
    mockMvc.perform(get(
      UriComponentsBuilder.fromPath("/paging-request")
        .queryParam(pagingProperties.getQuery().getSortByKey(), "foo:foo, bar:bar")
        .build()
        .toUri()
    )).andExpectAll(
      jsonPath("page").value(pagingProperties.getDefaultPage() - 1),
      jsonPath("pageSize").value(pagingProperties.getDefaultPageSize()),
      jsonPath("sortBy[0].propertyName").value("foo"),
      jsonPath("sortBy[0].direction").value(pagingProperties.getDefaultSortDirection().name()),
      jsonPath("sortBy[1].propertyName").value("bar"),
      jsonPath("sortBy[1].direction").value(pagingProperties.getDefaultSortDirection().name())
    ).andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testPageParamIsZeroFromPagingRequest() throws Exception {
    mockMvc.perform(get(
      UriComponentsBuilder.fromPath("/paging-request")
        .queryParam(pagingProperties.getQuery().getPageKey(), 0)
        .queryParam(pagingProperties.getQuery().getPageSizeKey(), 0)
        .build()
        .toUri()
    )).andExpectAll(
      status().isOk(),
      jsonPath("page").value(pagingProperties.getDefaultPage() - 1),
      jsonPath("pageSize").value(pagingProperties.getDefaultPageSize()),
      jsonPath("sortBy").isEmpty()
    ).andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @Test
  void testPageParamIsNegativeFromPagingRequest() throws Exception {
    mockMvc.perform(get(
      UriComponentsBuilder.fromPath("/paging-request")
        .queryParam(pagingProperties.getQuery().getPageKey(), Integer.MIN_VALUE)
        .queryParam(pagingProperties.getQuery().getPageSizeKey(), Integer.MIN_VALUE)
        .build()
        .toUri()
    )).andExpectAll(
      status().isOk(),
      jsonPath("page").value(pagingProperties.getDefaultPage() - 1),
      jsonPath("pageSize").value(pagingProperties.getDefaultPageSize()),
      jsonPath("sortBy").isEmpty()
    ).andDo(result -> log.info("RESPONSE: {}", result.getResponse().getContentAsString()));
  }

  @SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
  @ImportAutoConfiguration(value = {CommonAutoConfiguration.class})
  public static class Application {

    @RestController
    public static class ExampleController {

      @GetMapping(value = "/paging-request", produces = MediaType.APPLICATION_JSON_VALUE)
      public ResponseEntity<PagingRequest> pagingRequest(PagingRequest pagingRequest) {
        return ResponseEntity.ok(pagingRequest);
      }

      @GetMapping(value = "/paging", produces = MediaType.APPLICATION_JSON_VALUE)
      public ResponseEntity<PagingResponse> paging(PagingRequest pagingRequest) {
        return ResponseEntity.ok(
          PagingHelper.toPaging(
            pagingRequest,
            100,
            100L * pagingRequest.getPageSize()
          )
        );
      }

    }

  }

}
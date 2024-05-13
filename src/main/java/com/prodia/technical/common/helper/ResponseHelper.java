package com.prodia.technical.common.helper;

import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.model.response.PagingResponse;
import com.prodia.technical.common.model.response.WebResponse;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

public class ResponseHelper {

  public static <T> WebResponse<T> ok() {
    return ResponseHelper.ok(null);
  }

  public static <T> WebResponse<T> ok(T data) {
    return ResponseHelper.status(HttpStatus.OK, data, null, null, null);
  }

  public static <T> WebResponse<T> ok(T data, List<String> warning) {
    return ResponseHelper.status(HttpStatus.OK, data, null, null, 
        Map.ofEntries(Map.entry("warning", warning)));
  }
  
  public static <T> WebResponse<T> ok(T data, PagingResponse paging) {
    return ResponseHelper.status(HttpStatus.OK, data, paging);
  }

  public static <T> WebResponse<T> internalServerError() {
    return ResponseHelper.status(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static <T> WebResponse<T> unauthorized() {
    return ResponseHelper.status(HttpStatus.UNAUTHORIZED);
  }

  public static <T> WebResponse<T> badRequest(Map<String, List<String>> errors) {
    return ResponseHelper.status(HttpStatus.BAD_REQUEST, null, null, errors, null);
  }

  public static <T> WebResponse<T> status(HttpStatus status) {
    return ResponseHelper.status(status, null);
  }

  public static <T> WebResponse<T> status(HttpStatus status, T data) {
    return ResponseHelper.status(status, data, null);
  }

  public static <T> WebResponse<T> status(HttpStatus status, T data, PagingResponse paging) {
    return ResponseHelper.status(status, data, paging, null, null);
  }

  public static <T> WebResponse<T> status(HttpStatus status, T data, PagingResponse paging,
      Map<String, List<String>> errors, Map<String, Object> metadata) {
    return WebResponse.<T>builder()
        .code(status.value())
        .status(status.name())
        .paging(paging)
        .data(data)
        .errors(errors)
        .metadata(metadata)
        .build();
  }

  public static <T> WebResponse<List<T>> ok(PagingRequest pagingRequest, Page<T> data) {
    return ResponseHelper.status(HttpStatus.OK, data.getContent(), PagingHelper.toPaging(pagingRequest, data.getTotalElements()));
  }

}
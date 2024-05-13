package com.prodia.technical.logging.controller;

import com.prodia.technical.common.helper.PagingHelper;
import com.prodia.technical.common.helper.ResponseHelper;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.model.response.WebResponse;
import com.prodia.technical.logging.model.RestApiLogRequest;
import com.prodia.technical.logging.model.RestApiLogResponse;
import com.prodia.technical.logging.persistence.entity.EntityLogAggregate;
import com.prodia.technical.logging.persistence.entity.EntityLogDetail;
import com.prodia.technical.logging.service.EntityLogService;
import com.prodia.technical.logging.service.RestApiLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/api/v1"})
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LogController {

  private final RestApiLogService service;
  private final EntityLogService entityLogService;

  @GetMapping(value = {"/logs/rest-api"})
  public ResponseEntity<WebResponse<List<RestApiLogResponse>>> getAllRestApiLogByFilter(
      @RequestParam("user") String user, @RequestParam("start") Long start,
      @RequestParam("end") Long end, PagingRequest pagingRequest) {
    var results = service.findAllByFilter(pagingRequest, new RestApiLogRequest(user, start, end));
    return ResponseEntity.ok(ResponseHelper.ok(results.getContent(),
        PagingHelper.toPaging(pagingRequest, results.getTotalPages(), results.getTotalElements())));
  }

  @GetMapping(value = {"/logs/user"})
  public ResponseEntity<WebResponse<List<EntityLogAggregate>>> getAllUserLogByFilter(
      @RequestParam("start") Long start, @RequestParam("end") Long end,
      @RequestParam(value = "user", required = false) String user,
      @RequestParam(value = "module", required = false) String module,
      PagingRequest pagingRequest) {
    module = (module != null && module.isEmpty()) ? null : module;

    var results = entityLogService.findAllByFilter(pagingRequest, start, end, user, module);
    return ResponseEntity.ok(ResponseHelper.ok(results.getContent(), PagingHelper
        .toPaging(pagingRequest, results.getNumber(), (long) results.getNumberOfElements())));
  }

  @GetMapping(value = {"/log/user/detail"})
  public ResponseEntity<WebResponse<List<EntityLogDetail>>> getUserLogDetail(
      @RequestParam("user") String user, @RequestParam("date") String date,
      @RequestParam("module") String module, @RequestParam("entityName") String entityName,
      @RequestParam("action") String action, PagingRequest pagingRequest) {
    var results = entityLogService.getDetail(pagingRequest, date, user, module, entityName, action);
    return ResponseEntity.ok(ResponseHelper.ok(results.getContent(), PagingHelper
        .toPaging(pagingRequest, results.getNumber(), (long) results.getNumberOfElements())));
  }
}

package com.prodia.technical.logging.service;

import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.logging.model.RestApiLogRequest;
import com.prodia.technical.logging.model.RestApiLogResponse;
import org.springframework.data.domain.Page;

public interface RestApiLogService {

  Page<RestApiLogResponse> findAllByFilter(PagingRequest pagingRequest, RestApiLogRequest restApiLogFilter);

}
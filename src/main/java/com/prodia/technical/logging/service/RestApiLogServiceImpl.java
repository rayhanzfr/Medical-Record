package com.prodia.technical.logging.service;

import com.prodia.technical.common.helper.error.ConstraintValidationException;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.validation.ValidationHelper;
import com.prodia.technical.logging.model.RestApiLogRequest;
import com.prodia.technical.logging.model.RestApiLogResponse;
import com.prodia.technical.logging.persistence.entity.ApiLog;
import com.prodia.technical.logging.persistence.repository.ApiLogRepository;
import jakarta.validation.ConstraintViolation;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestApiLogServiceImpl implements RestApiLogService {

  private final ApiLogRepository apiLogRepository;
  private final ValidationHelper validationHelper;

  @Override
  public Page<RestApiLogResponse> findAllByFilter(PagingRequest pagingRequest, RestApiLogRequest filter) {
    Set<? extends ConstraintViolation<?>> constraintViolations = validationHelper.getConstraintViolation(filter);
    if (filter.getStartEpoch() != null && filter.getEndEpoch() != null &&
        (filter.getStartEpoch() > filter.getEndEpoch())) {
      Map<String, List<String>> errors = new HashMap<>();
      errors.put("startEpoch", Collections.singletonList("start date must be before than end date"));
      errors.put("endEpoch", Collections.singletonList("end date must be after than start date"));
      throw new ConstraintValidationException(constraintViolations, errors);
    }

    ZonedDateTime startDateTime = Optional.ofNullable(filter.getStartEpoch())
      .map(dateTime -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault()))
      .orElse(ZonedDateTime.of(LocalDate.now().minusDays(31), LocalTime.MIN, ZoneId.systemDefault()));
    ZonedDateTime endDateTime = Optional.ofNullable(filter.getEndEpoch())
      .map(dateTime -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault()))
      .orElse(ZonedDateTime.of(LocalDate.now(), LocalTime.MAX, ZoneId.systemDefault()));

    var pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize());
    var apiLogs = apiLogRepository.findAllBy(
      filter.getUser(),
      "d38877ec-5d7a-4fba-a7f2-f9138b6c36a0",
      startDateTime.toInstant(),
      endDateTime.toInstant(),
      pageable
    );
    return new PageImpl<>(
      apiLogs.getContent()
        .stream()
        .map(this::mapEntityToDto)
        .toList(), pageable,
      apiLogs.getTotalElements());
  }

  private RestApiLogResponse mapEntityToDto(ApiLog entity) {
    return RestApiLogResponse.builder()
      .applicationName(entity.getApplicationName())
      .httpMethod(entity.getHttpMethod())
      .protocol(entity.getProtocol())
      .uri(entity.getUri())
      .queryParam(entity.getQueryParam())
      .request(entity.getRequest())
      .response(entity.getResponse())
      .timestamp(entity.getTimestamp().toEpochMilli())
      .build();
  }
}
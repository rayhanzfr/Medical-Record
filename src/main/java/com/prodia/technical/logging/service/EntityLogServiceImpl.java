package com.prodia.technical.logging.service;

import com.prodia.technical.common.helper.error.ConstraintValidationException;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.logging.persistence.entity.EntityLogAggregate;
import com.prodia.technical.logging.persistence.entity.EntityLogDetail;
import com.prodia.technical.logging.persistence.repository.EntityLogRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntityLogServiceImpl implements EntityLogService {

  private final EntityLogRepository repository;

  @Override
  public Slice<EntityLogAggregate> findAllByFilter(PagingRequest pagingRequest, Long startEpoch,
      Long endEpoch, String user, String module) {
    if (startEpoch != null && endEpoch != null && (startEpoch > endEpoch)) {
      Map<String, List<String>> errors = new HashMap<>();
      errors.put("startEpoch",
          Collections.singletonList("start date must be before than end date"));
      errors.put("endEpoch", Collections.singletonList("end date must be after than start date"));
      throw new ConstraintValidationException(Collections.emptySet(), errors);
    }

    ZonedDateTime startDateTime = Optional.ofNullable(startEpoch).map(
        dateTime -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault()))
        .orElse(
            ZonedDateTime.of(LocalDate.now().minusDays(31), LocalTime.MIN, ZoneId.systemDefault()));
    ZonedDateTime endDateTime = Optional.ofNullable(endEpoch).map(
        dateTime -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault()))
        .orElse(ZonedDateTime.of(LocalDate.now(), LocalTime.MAX, ZoneId.systemDefault()));

    var pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize());
    var entityLogs = repository.findAllByFilter(null,
        startDateTime.toInstant(), endDateTime.toInstant(), user, module, pageable);
    return entityLogs;
  }

  @Override
  public Slice<EntityLogDetail> getDetail(PagingRequest pagingRequest, String date, String user,
      String module, String entityName, String action) {
    var pageable = PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize());
    var entityLogs = repository.findDetailBy(null, date, user,
        module, entityName, action, pageable);
    return entityLogs;
  }

}

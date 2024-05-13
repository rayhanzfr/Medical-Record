package com.prodia.technical.logging.service;

import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.logging.persistence.entity.EntityLogAggregate;
import com.prodia.technical.logging.persistence.entity.EntityLogDetail;
import org.springframework.data.domain.Slice;

public interface EntityLogService {

  Slice<EntityLogAggregate> findAllByFilter(PagingRequest pagingRequest, Long startEpoch,
      Long endEpoch, String user, String module);

  Slice<EntityLogDetail> getDetail(PagingRequest pagingRequest, String date, String user,
      String module, String entityName, String action);

}

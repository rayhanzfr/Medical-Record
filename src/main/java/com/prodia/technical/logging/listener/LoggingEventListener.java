package com.prodia.technical.logging.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.prodia.technical.common.helper.ReflectionHelper;
import com.prodia.technical.logging.event.AbstractLogEvent;
import com.prodia.technical.logging.event.ApiLogEvent;
import com.prodia.technical.logging.event.EntityAuditTrailEvent;
import com.prodia.technical.logging.persistence.entity.AbstractLogDocument;
import com.prodia.technical.logging.persistence.entity.ApiLog;
import com.prodia.technical.logging.persistence.entity.EntityLog;
import com.prodia.technical.logging.persistence.repository.ApiLogRepository;
import com.prodia.technical.logging.persistence.repository.EntityLogRepository;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class LoggingEventListener {

  private static final String DEFAULT_SEPARATOR_PREFIX = "[";
  private static final String DEFAULT_SEPARATOR_SUFFIX = "]";

  private final EntityLogRepository entityLogRepository;
  private final ApiLogRepository apiLogRepository;

  @Value("${spring.application.name}")
  private String appName;

  @Value("${prodia.logging.persistence}")
  private Boolean isPersistenceLogEnabled;

  @Value("${prodia.logging.rest-api}")
  private Boolean isRestApiLogEnabled;

  @EventListener(EntityAuditTrailEvent.class)
  public void handleAuditTrailEvent(EntityAuditTrailEvent event) {
    if (Boolean.FALSE.equals(isPersistenceLogEnabled)) {
      return;
    }

    var entityLogBuilder = EntityLog.builder().action(event.getAction().toString());
    var entityClass = event.getEntity().getClass();
    entityLogBuilder.module(ReflectionHelper.getModuleNameFromPackage(entityClass.getPackageName(), appName))
      .tableName(ReflectionHelper.getTableNameFromEntityClass(entityClass))
      .entityName(entityClass.getSimpleName());

    ObjectMapper oMapper = new ObjectMapper();
    oMapper.findAndRegisterModules();
    oMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    var fields = ReflectionHelper.getAllFields(entityClass, new LinkedList<>());
    Map<String, Object> fieldMap = ReflectionHelper.getChunkedProperties(fields, event.getEntity());
    Map<String, Object> properties = oMapper.convertValue(fieldMap, new TypeReference<Map<String, Object>>() {
    });
    entityLogBuilder.entity(new Document(properties));

    var entityLog = entityLogBuilder.build();
    setupLogDocumentProperties(event, entityLog);
    entityLogRepository.save(entityLog);
    log.info("[{}] {} {} ({})\n{}", event.getEntity().hashCode(), event.getAction(), entityLog.getEntityName(),
      entityLog.getTableName(), properties);
  }

  @EventListener(ApiLogEvent.class)
  public void handleApiLogEvent(ApiLogEvent event) {
    if (Boolean.FALSE.equals(isRestApiLogEnabled)) {
      return;
    }

    var apiLog = ApiLog.builder()
      .protocol(event.getProtocol())
      .httpMethod(event.getHttpMethod())
      .uri(event.getUri())
      .queryParam(event.getQueryParam())
      .request(event.getRequest())
      .response(event.getResponse())
      .build();
    setupLogDocumentProperties(event, apiLog);
    apiLogRepository.save(apiLog);

    var requestId = Optional.ofNullable(event.getRequestId())
      .map(id -> DEFAULT_SEPARATOR_PREFIX + id + DEFAULT_SEPARATOR_SUFFIX)
      .orElse("");
    log.info("{} {} {} {}{}", requestId, event.getProtocol(), event.getHttpMethod(), event.getUri(),
      Optional.ofNullable(event.getQueryParam()).orElse(""));
    log.info("{} Response {}", requestId, event.getStatusCode());
  }

  private void setupLogDocumentProperties(AbstractLogEvent event, AbstractLogDocument logDocument) {
    logDocument.setApplicationName(this.appName);
    logDocument.setTimestamp(Instant.ofEpochMilli(event.getTimestamp()));
    logDocument.setTenantId(event.getTenantId());
    logDocument.setUser(event.getUser());
  }
}

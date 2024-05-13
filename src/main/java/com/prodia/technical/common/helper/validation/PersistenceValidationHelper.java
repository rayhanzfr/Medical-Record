package com.prodia.technical.common.helper.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PersistenceValidationHelper {
  @PersistenceContext
  private EntityManager entityManager;

  public <T> String getTableNames(Class<T> entityClass) {
    Table t = entityClass.getAnnotation(Table.class);
    if (t == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          entityClass.getSimpleName() + " is not entity.");
    } else {
      return t.name();
    }
  }

  @Transactional
  public boolean isUsedAsFk(Class<?> entityClass, String id, List<Class<?>> excludedEntityClasses) {
    String tableName = getTableNames(entityClass);
    List<String> excludedTableNames = excludedEntityClasses == null ? new ArrayList<>()
        : excludedEntityClasses.stream().map(this::getTableNames).toList();
    return (boolean) entityManager
        .createNativeQuery("SELECT is_used_as_foreign_key(:tableName, :id, :excludedTableNames)")
        .setParameter("tableName", tableName).setParameter("id", id)
        .setParameter("excludedTableNames", excludedTableNames.stream().toArray(String[] ::new))
        .getSingleResult();
  }
}
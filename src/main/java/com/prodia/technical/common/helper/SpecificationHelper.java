package com.prodia.technical.common.helper;

import com.prodia.technical.common.annotation.SortProperty;
import com.prodia.technical.common.helper.error.ErrorMessageConstant;
import com.prodia.technical.common.model.request.SortBy;
import com.prodia.technical.common.model.request.SortByDirection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecificationHelper {


  @SuppressWarnings("unchecked")
  private static <T, Y> Join<T, Y> getJoin(Root<T> root, String column) {
    return (Join<T, Y>) root.getJoins().stream()
        .filter(
            r -> r.getAttribute().getName().equals(column) && r.getJoinType().equals(JoinType.LEFT))
        .findFirst().orElseGet(() -> root.join(column, JoinType.LEFT));
  }

  @SuppressWarnings("unchecked")
  private static <T, Y> Join<T, Y> getJoin(Join<T, Y> root, String column) {
    return (Join<T, Y>) root.getJoins().stream()
        .filter(
            r -> r.getAttribute().getName().equals(column) && r.getJoinType().equals(JoinType.LEFT))
        .findFirst().orElseGet(() -> root.join(column, JoinType.LEFT));
  }

  private static <T> Path<T> getRootPath(String column, Root<T> root) {
    if (!column.contains(".")) {
      return root.get(column);
    }
    String joinColumn = StringUtils.substringBefore(column, ".");
    column = StringUtils.substringAfter(column, ".");
    Join<T, ?> path = getJoin(root, joinColumn);
    while (true) {
      if (column.contains(".")) {
        joinColumn = StringUtils.substringBefore(column, ".");
        column = StringUtils.substringAfter(column, ".");
        path = getJoin(path, joinColumn);
      } else {
        return path.get(column);
      }
    }
  }

  private static <T> Expression<T> getRootExpression(String column, Root<T> root) {
    if (!column.contains(".")) {
      return root.get(column);
    }
    String joinColumn = StringUtils.substringBefore(column, ".");
    column = StringUtils.substringAfter(column, ".");
    Join<T, ?> path = getJoin(root, joinColumn);
    while (true) {
      if (column.contains(".")) {
        joinColumn = StringUtils.substringBefore(column, ".");
        column = StringUtils.substringAfter(column, ".");
        path = getJoin(path, joinColumn);
      } else {
        return path.get(column);
      }
    }
  }

  public static <T> Specification<T> inquiryFilter(List<String> columns, String inquiry) {
    return ((root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      for (String column : columns) {
        Path<T> path = getRootPath(column, root);
        predicates.add(cb.and(
            cb.like(cb.lower(path.as(String.class)), "%|" + inquiry.toLowerCase() + "%", '|')));
      }
      return cb.or(predicates.toArray(new Predicate[0]));
    });
  }

  public static <T> Specification<T> parameterFilter(String column, Object value) {
    return ((root, query, cb) -> {
      Path<T> path = getRootPath(column, root);
      return cb.equal(path, value);
    });
  }

  public static <T> Specification<T> parameterFilter(String column, Object value,
      SpecificationOperation operator) {
    return ((root, query, cb) -> {
      Path<T> path = getRootPath(column, root);
      return createPredicate(cb, path, value, operator);
    });
  }

  public static <T> Specification<T> parameterFilterIfNotNull(String column, Object value,
      SpecificationOperation operator) {
    return ((root, query, cb) -> {
      Path<T> path = getRootPath(column, root);
      Expression<Boolean> expression = cb.<Boolean>selectCase()
          .when(path.isNotNull(), createPredicate(cb, path, value, operator)).otherwise(true);
      return cb.and(expression, cb.literal(true));

    });
  }

  public static <T> Specification<T> ifNotNullEqualTo(String column, Object value) {
    return ((root, query, cb) -> {
      Path<T> path = getRootPath(column, root);

      Expression<Boolean> expression =
          cb.<Boolean>selectCase().when(path.isNotNull(), cb.equal(path, value)).otherwise(true);
      return cb.and(expression, cb.literal(true));
    });
  }

  public static <T> Specification<T> likeAny(String column, Object value) {
    return ((root, query, cb) -> {
      Expression<T> pathArray = getRootExpression(column, root);
      return cb.like(
          cb.lower(
              cb.function("array_to_string", String.class, pathArray, cb.<String>literal("|"))),
          "%|" + ((String) value).toLowerCase() + "%", '|');
    });
  }

  public static <T> Specification<T> parameterFilterIn(String column, List<?> values) {
    return ((root, query, cb) -> {
      Path<T> path = getRootPath(column, root);
      return path.in(values);
    });
  }

  public static <T> Specification<T> whereTrue() {
    return ((root, query, cb) -> cb.conjunction());
  }

  public static <T> Specification<T> whereFalse() {
    return ((root, query, cb) -> cb.disjunction());
  }

  public static <T> Specification<T> parameterFilterNotIn(String column, List<?> values) {
    return ((root, query, cb) -> {
      Path<T> path = getRootPath(column, root);
      return path.in(values).not();
    });
  }

  public static <T> Specification<T> isNull(String column) {
    return ((root, query, cb) -> {
      Path<T> path = getRootPath(column, root);
      return path.isNull();
    });
  }

  public static <T> Specification<T> isNotNull(String column) {
    return ((root, query, cb) -> {
      Path<T> path = getRootPath(column, root);
      return path.isNotNull();
    });
  }

  public static <T> Specification<T> filter(String inquiry, Boolean isActive, String... columns) {
    Specification<T> spec = Specification.where(null);
    if (inquiry != null && !inquiry.isBlank() && columns != null && columns.length > 0) {
      spec = spec.and(inquiryFilter(Arrays.asList(columns), inquiry));
    }
    if (isActive != null) {
      spec = spec.and(parameterFilter("isActive", isActive));
    }
    return spec;
  }

  private static List<Field> getAllBaseField(Class<?> entity) {
    Class<?> base = entity;
    List<Field> list = new ArrayList<>();
    list.addAll(Arrays.asList(base.getDeclaredFields()));

    boolean isBreak = false;
    while (!isBreak) {
      try {
        base = base.getSuperclass();
        list.addAll(Arrays.asList(base.getDeclaredFields()));
      } catch (Exception e) {
        isBreak = true;
      }
    }
    return list;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static <Y extends Comparable<? super Y>> Predicate createPredicate(CriteriaBuilder cb,
      Expression column, Object value, SpecificationOperation operator) {
    switch (operator) {
      case EQUAL:
        return cb.equal(column, value);
      case GREATER_THAN:
        return cb.greaterThan(column, (Y) value);
      case GREATER_THAN_EQUAL:
        return cb.greaterThanOrEqualTo(column, (Y) value);
      case LESS_THAN:
        return cb.lessThan(column, (Y) value);
      case LESS_THAN_EQUAL:
        return cb.lessThanOrEqualTo(column, (Y) value);
      case MATCH:
        return cb.like(column, "%" + (String) value + "%");
      case MATCH_END:
        return cb.like(cb.lower(column), "%" + ((String) value).toLowerCase() + "%");
      case NOT_EQUAL:
        return cb.notEqual(column, value);
      case IN:
        return column.in((List<?>) value);
      default:
        return cb.equal(column, value);
    }
  }

  public enum SpecificationOperation {
    GREATER_THAN, LESS_THAN, GREATER_THAN_EQUAL, LESS_THAN_EQUAL, NOT_EQUAL, EQUAL, MATCH, MATCH_END, IN
  }

  public static Sort createSort(Map<String, String> properties, List<SortBy> sortBy) {
    List<Order> orders = new ArrayList<>();
    if (sortBy == null || sortBy.isEmpty()) {
      return Sort.by(orders);
    }
    sortBy.stream().forEach(s -> {
      if (properties.get(s.getPropertyName()) == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "sort property " + ErrorMessageConstant.IS_NOT_EXISTS);
      }
      orders.add(new Order(Direction.fromString(s.getDirection().name()),
          properties.get(s.getPropertyName())));
    });
    return Sort.by(orders);
  }

  public static Sort createSort(List<SortBy> sortBy) {
    List<Order> orders = new ArrayList<>();
    if (sortBy == null || sortBy.isEmpty()) {
      return Sort.by(orders);
    }
    sortBy.stream().forEach(s -> orders
        .add(new Order(Direction.fromString(s.getDirection().name()), s.getPropertyName())));
    return Sort.by(orders);
  }

  public static Sort createSort(String property, SortByDirection direction) {
    return Sort.by(Arrays.asList(new Order(Direction.fromString(direction.name()), property)));
  }

  public static Sort createSort(Class<?> clazz, List<SortBy> sortBy) {
    List<Field> list = getAllBaseField(clazz);
    List<Order> orders = new ArrayList<>();
    sortBy.stream().forEach(s -> list.stream().filter(f -> f.getName().equals(s.getPropertyName()))
        .findFirst().ifPresentOrElse(f -> {
          SortProperty sortProperty = f.getDeclaredAnnotation(SortProperty.class);
          if (sortProperty == null) {
            orders
                .add(new Order(Direction.fromString(s.getDirection().name()), s.getPropertyName()));
          } else {
            orders.add(new Order(Direction.fromString(s.getDirection().name()),
                sortProperty.entityProperty()));
          }
        }, () -> {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "sort property " + ErrorMessageConstant.IS_NOT_EXISTS);
        }));
    return Sort.by(orders);
  }

  public static CompareToBuilder createBuilderSort() {
    CompareToBuilder builder = new CompareToBuilder();
    return builder;
  }

  public static <T> Comparator<T> createModelSort(Class<T> clazz, Sort sort) {
    return (val1, val2) -> {
      CompareToBuilder builder = new CompareToBuilder();
      List<Field> list = getAllBaseField(clazz);
      sort.forEach(s -> list.stream().filter(f -> f.getName().equals(s.getProperty())).findFirst()
          .ifPresent(f -> {
            if (s.getDirection().equals(Direction.ASC)) {
              try {
                builder.append(FieldUtils.readField(val1, f.getName(), true),
                    FieldUtils.readField(val2, f.getName(), true));
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
            } else if (s.getDirection().equals(Direction.DESC)) {
              try {
                builder.append(FieldUtils.readField(val2, f.getName(), true),
                    FieldUtils.readField(val1, f.getName(), true));
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              }
            }
          }));
      return builder.toComparison();
    };
  }

  public static Order createOrder(String property, SortByDirection direction) {
    return new Order(Direction.fromString(direction.name()), property);
  }
}

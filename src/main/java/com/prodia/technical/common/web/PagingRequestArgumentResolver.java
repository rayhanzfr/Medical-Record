package com.prodia.technical.common.web;

import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.model.request.SortBy;
import com.prodia.technical.common.model.request.SortByDirection;
import com.prodia.technical.common.properties.PagingProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@AllArgsConstructor
public class PagingRequestArgumentResolver implements HandlerMethodArgumentResolver {

  public static final String SORT_BY_SEPARATOR = ":";
  public static final String SORT_BY_SPLITTER = ",";
  public static final String EMPTY_STRING = "";

  private final PagingProperties pagingProperties;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return PagingRequest.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    return fromHttpServletRequest(
        Objects.requireNonNull(webRequest.getNativeRequest(HttpServletRequest.class))
    );
  }

  private PagingRequest fromHttpServletRequest(HttpServletRequest request) {
    PagingRequest paging = new PagingRequest();

    paging.setPage(getInt(
        request.getParameter(pagingProperties.getQuery().getPageKey()),
        pagingProperties.getDefaultPage()
    ) - 1);
    paging.setPageSize(getInt(
        request.getParameter(pagingProperties.getQuery().getPageSizeKey()),
        pagingProperties.getDefaultPageSize()
    ));

    if (paging.getPageSize() > pagingProperties.getMaxPageSize()) {
      paging.setPageSize(pagingProperties.getMaxPageSize());
    }
    paging.setSortBy(getSortByList(
        request.getParameter(pagingProperties.getQuery().getSortByKey()),
        pagingProperties
    ));
    return paging;
  }

  private Integer getInt(String value, Integer defaultValue) {
    if (value == null) {
      return defaultValue;
    } else {
      return toInt(value, defaultValue);
    }
  }

  private List<SortBy> getSortByList(String value, PagingProperties pagingProperties) {
    if (value == null || value.isBlank()) {
      return Collections.emptyList();
    } else {
      return toSortByList(value, pagingProperties);
    }
  }

  public List<SortBy> toSortByList(String request, PagingProperties pagingProperties) {
    return Arrays.stream(request.split(SORT_BY_SPLITTER))
        .map(s -> toSortBy(s, pagingProperties))
        .filter(Objects::nonNull)
        .filter(sortBy -> Objects.nonNull(sortBy.getPropertyName()))
        .toList();
  }


  public SortBy toSortBy(String request, PagingProperties pagingProperties) {
    String sort = request.trim();
    if (sort.replace(SORT_BY_SEPARATOR, EMPTY_STRING).isEmpty() || sort.startsWith(
        SORT_BY_SEPARATOR)) {
      return null;
    }

    String[] sortBy = sort.split(SORT_BY_SEPARATOR);
    return new SortBy(
        getAt(sortBy, 0, null),
        Optional.ofNullable(EnumUtils.getEnum(SortByDirection.class,
                getAt(sortBy, 1, pagingProperties.getDefaultSortDirection().name()).toUpperCase()))
            .orElse(pagingProperties.getDefaultSortDirection())
    );
  }

  public String getAt(String[] strings, int index, String defaultValue) {
    return strings.length <= index ? defaultValue : strings[index];
  }

  public Integer toInt(String value, Integer defaultValue) {
    try {
      var intValue = Integer.parseInt(value);
      return intValue <= 0 ? defaultValue : intValue;
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }
}
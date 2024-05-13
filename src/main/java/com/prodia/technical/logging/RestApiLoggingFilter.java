package com.prodia.technical.logging;

import com.prodia.technical.authentication.helper.SessionHelper;
import com.prodia.technical.authentication.model.UserPrincipal;
import com.prodia.technical.authentication.persistence.entity.User;
import com.prodia.technical.logging.event.ApiLogEvent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@RequiredArgsConstructor
public class RestApiLoggingFilter extends OncePerRequestFilter {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    ContentCachingRequestWrapper requestWrapper = wrapRequest(request);
    ContentCachingResponseWrapper responseWrapper = wrapResponse(response);

    try {
      filterChain.doFilter(requestWrapper, responseWrapper);
    } finally {
      if (!isAsyncStarted(requestWrapper)) {
        var apiLogEvent = buildApiLogEvent(requestWrapper, responseWrapper);
        responseWrapper.copyBodyToResponse();
        apiLogEvent.getResponse().put("header", getHeaders(responseWrapper));
        applicationEventPublisher.publishEvent(apiLogEvent);
      }
    }
  }

  private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
    return request instanceof ContentCachingRequestWrapper requestWrapper ? requestWrapper
        : new ContentCachingRequestWrapper(request);
  }

  private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
    return response instanceof ContentCachingResponseWrapper responseWrapper ? responseWrapper
        : new ContentCachingResponseWrapper(response);
  }

  private ApiLogEvent buildApiLogEvent(ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response) throws IOException, ServletException {
    var apiLogEvent = new ApiLogEvent(this);
    apiLogEvent.setRequestId(request.getRequestId());
    apiLogEvent.setProtocol(request.getProtocol());
    apiLogEvent.setHttpMethod(request.getMethod());
    apiLogEvent.setUri(String.valueOf(request.getRequestURL()));
    apiLogEvent.setQueryParam(getQueryParam(request));
    apiLogEvent.setStatusCode(HttpStatus.valueOf(response.getStatus()));

    var requestDocument = new Document();
    requestDocument.put("header", getHeaders(request));
    requestDocument.put("body", getBodyPayload(request));
    apiLogEvent.setRequest(requestDocument);

    var responseDocument = new Document();
    responseDocument.put("body", getBodyPayload(response));
    apiLogEvent.setResponse(responseDocument);

    UserPrincipal userPrinciple = SessionHelper.getUserPrincipal();
    User user = userPrinciple.getUser();
    if (user != null) {
      apiLogEvent.setTenantId(null);
        apiLogEvent.setUser(user.getUsername());
     } else {
       apiLogEvent.setTenantId(null);
       apiLogEvent.setUser(null);
    }
    return apiLogEvent;
  }

  private Map<String, String> getHeaders(HttpServletRequest request) {
    return getHeaders(request.getHeaderNames(), name -> Collections.list(request.getHeaders(name)));
  }

  private Map<String, String> getHeaders(ContentCachingResponseWrapper response) {
    return getHeaders(Collections.enumeration(response.getHeaderNames()), response::getHeaders);
  }

  private Map<String, String> getHeaders(Enumeration<String> enumeration,
      Function<String, Collection<String>> headerProvider) {
    Map<String, String> result = new HashMap<>();
    while (enumeration.hasMoreElements()) {
      var key = enumeration.nextElement();
      var value = headerProvider.apply(key);
      result.put(key, String.join(", ", value));
    }
    return !result.isEmpty() ? result : null;
  }

  private String getQueryParam(HttpServletRequest request) {
    return Optional.ofNullable(request.getQueryString())
        .map(query -> '?' + URLDecoder.decode(query, StandardCharsets.UTF_8)).orElse(null);
  }

  private BsonValue getBodyPayload(ContentCachingRequestWrapper request)
      throws IOException, ServletException {
    if (request.getContentLength() <= 0) {
      return BsonNull.VALUE;
    }

    if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
      return parseStringJsonToBson(
          new String(request.getContentAsByteArray(), request.getCharacterEncoding()));
    } else if (request.getContentType().startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
      return parseMultiPartToBson(request.getParts());
    } else {
      return new BsonString(
          new String(request.getContentAsByteArray(), request.getCharacterEncoding()));
    }
  }

  private BsonValue getBodyPayload(ContentCachingResponseWrapper response)
      throws UnsupportedEncodingException {
    if (response.getContentType() != null
        && response.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
      return parseStringJsonToBson(
          new String(response.getContentAsByteArray(), response.getCharacterEncoding()));
    } else {
      return BsonNull.VALUE;
    }
  }

  private BsonValue parseStringJsonToBson(String json) {
    if (BsonUtils.isJsonArray(json)) {
      return BsonArray.parse(json);
    } else if (BsonUtils.isJsonDocument(json)) {
      return BsonDocument.parse(json);
    } else {
      System.out.println("Json string :: " + json);
      logger.error("Invalid json format for request body : " + json);
      return new BsonString(json);
    }
  }

  private BsonValue parseMultiPartToBson(Collection<Part> parts) throws IOException {
    var bsonArray = new BsonArray();
    for (Part part : parts) {
      var bsonDocument = new BsonDocument();
      String key = part.getName();
      BsonValue value;
      if (part.getContentType() == null) {
        value = parseStringJsonToBson(new String(part.getInputStream().readAllBytes()));
      } else if (part.getContentType().equals(MediaType.TEXT_PLAIN_VALUE)
          || part.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
        value = parseStringJsonToBson(new String(part.getInputStream().readAllBytes()));
      } else {
        var document = new Document(
            getHeaders(Collections.enumeration(part.getHeaderNames()), part::getHeaders));
        value = document.toBsonDocument();
      }
      bsonDocument.put(key, value);
      bsonArray.add(bsonDocument);
    }
    return bsonArray;
  }

}

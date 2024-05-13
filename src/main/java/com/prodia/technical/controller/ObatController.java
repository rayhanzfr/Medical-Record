package com.prodia.technical.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodia.technical.authentication.model.request.OAuth2Response;
import com.prodia.technical.common.helper.ResponseHelper;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.model.response.WebResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ObatController {

  private final String GET_KFA2 = "https://api-satusehat-stg.dto.kemkes.go.id/kfa-v2/products/all";
  @Value("${spring.security.oauth2.client.provider.ihs.token-uri}") String tokenUri;
  @Value("${spring.security.oauth2.client.registration.ihs.client-id}") String clientId;
  @Value("${spring.security.oauth2.client.registration.ihs.client-secret}") String clientSecret;
  @Value("${spring.security.oauth2.client.registration.ihs.authorization-grant-type}") String authorizationGrantType;

  private static RestTemplate restTemplate = new RestTemplate();

  @GetMapping("/obat")
  public <T> ResponseEntity<T> callListKFA(PagingRequest pagingRequest,@RequestParam(required = false) String keyword){

    //getToken from OAuth2 Satu Sehat
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("client_id",clientId);
    requestBody.add("client_secret",clientSecret);
    HttpHeaders headers1 = new HttpHeaders();
    headers1.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    String urlTemplate1 = UriComponentsBuilder.fromUriString(tokenUri)
        .queryParam("grant_type",authorizationGrantType)
        .encode()
        .toUriString();
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers1);
    String body = restTemplate.postForEntity(
        urlTemplate1,requestEntity, String.class).getBody();
    Map<String,Object> map;
    try{
      map = new ObjectMapper().readValue(body, HashMap.class);
    }catch (JsonProcessingException e){
      return (ResponseEntity<T>) ResponseEntity.badRequest().body(e);
    }

    //get From KFA-2
    /* There's a simple API
    https://api-satusehat-stg.dto.kemkes.go.id/kfa/products/farmalkes-price-jkn
    but there's API forbidden in any way

    so there's still another api to get the pharmacy product that i used below
     */
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", String.format("Bearer %s", map.get("access_token")));

    String urlTemplate = UriComponentsBuilder.fromUriString(GET_KFA2)
        // Add query parameter
        .queryParam("page", pagingRequest.getPage()+1)
        .queryParam("size", pagingRequest.getPageSize())
        .queryParam("product_type","farmasi")
        .toUriString();
    if (keyword!=null && !keyword.isBlank()){
      urlTemplate = UriComponentsBuilder.fromUriString(urlTemplate)
          .queryParam("keyword",keyword).toUriString();
    }

    HttpEntity<String> request = new HttpEntity<String>(headers);
    return (ResponseEntity<T>) restTemplate.exchange(urlTemplate, HttpMethod.GET,request,String.class);
  }
}

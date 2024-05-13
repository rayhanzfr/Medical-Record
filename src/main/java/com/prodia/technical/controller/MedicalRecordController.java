package com.prodia.technical.controller;

import com.prodia.technical.common.helper.ResponseHelper;
import com.prodia.technical.common.helper.error.ErrorMessageConstant;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.model.response.WebResponse;
import com.prodia.technical.model.request.create.CreateMedicalRecordRequest;
import com.prodia.technical.model.response.MedicalRecordResponse;
import com.prodia.technical.service.MedicalRecordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class MedicalRecordController {

  private final MedicalRecordService service;
  private static final String MSG_MEDICAL_RECORD = "Medical Record(s)";

  @GetMapping(value = "/medical-records", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WebResponse<List<MedicalRecordResponse>>> getHeaderInformation(PagingRequest pagingRequest) {
    return ResponseEntity.ok(ResponseHelper.ok(pagingRequest, service.getAll(pagingRequest)));
  }

  @PostMapping(value = "/medical-records", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WebResponse<String>> add(@RequestBody CreateMedicalRecordRequest request) {
    service.add(request);
    return ResponseEntity
        .ok(ResponseHelper.ok(MSG_MEDICAL_RECORD + ErrorMessageConstant.HAS_BEEN_ADDED_SUCCESSFULLY));
  }

  @GetMapping(value = "/medical-records/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<WebResponse<MedicalRecordResponse>> getById(@PathVariable String id) {
    return ResponseEntity.ok(ResponseHelper.ok(service.getById(id)));
  }
}

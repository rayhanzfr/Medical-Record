package com.prodia.technical.service;

import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.model.request.create.CreateMedicalRecordRequest;
import com.prodia.technical.model.response.MedicalRecordResponse;
import com.prodia.technical.persistence.entity.MedicalRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface MedicalRecordService {

  void add(CreateMedicalRecordRequest request);

  Optional<MedicalRecord> getEntityById(String id);

  List<MedicalRecord> getAllEntity();

  Page<MedicalRecordResponse> getAll(PagingRequest pagingRequest);
  List<MedicalRecordResponse> getAll();
  MedicalRecordResponse getById(String id);
}

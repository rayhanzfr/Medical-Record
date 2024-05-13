package com.prodia.technical.service;

import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.model.request.create.CreateDiagnoseRequest;
import com.prodia.technical.model.request.update.UpdateDiagnoseRequest;
import com.prodia.technical.model.response.DiagnoseResponse;
import com.prodia.technical.persistence.entity.Diagnose;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface DiagnoseService {

  void add(CreateDiagnoseRequest request);
  void edit(UpdateDiagnoseRequest request);

  Optional<Diagnose> getEntityById(String id);

  List<Diagnose> getAllEntity();

  Page<DiagnoseResponse> getAll(PagingRequest pagingRequest);
  List<DiagnoseResponse> getAll(String medicalRecordId);
}

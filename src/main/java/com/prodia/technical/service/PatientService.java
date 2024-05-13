package com.prodia.technical.service;

import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.model.request.create.CreatePatientRequest;
import com.prodia.technical.model.request.update.UpdatePatientRequest;
import com.prodia.technical.model.response.PatientResponse;
import com.prodia.technical.persistence.entity.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PatientService {

  Patient add(CreatePatientRequest request);
  void edit(UpdatePatientRequest request);

  Optional<Patient> getEntityById(String id);
  Optional<Patient> getEntity(String name,String email);
  List<Patient> getAllEntity();

  Page<PatientResponse> getAll(PagingRequest pagingRequest);
  List<PatientResponse> getAll();
  PatientResponse get(String id);

}

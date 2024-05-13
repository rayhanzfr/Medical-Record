package com.prodia.technical.service;

import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.model.request.create.CreateRecipeMedicineRequest;
import com.prodia.technical.model.request.update.UpdateRecipeMedicineRequest;
import com.prodia.technical.model.response.RecipeMedicineResponse;
import com.prodia.technical.persistence.entity.RecipeMedicine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface RecipeMedicineService {

  void add(CreateRecipeMedicineRequest request);
  void edit(UpdateRecipeMedicineRequest request);

  Optional<RecipeMedicine> getEntityById(String id);

  List<RecipeMedicine> getAllEntity();

  Page<RecipeMedicineResponse> getAll(PagingRequest pagingRequest);
  List<RecipeMedicineResponse> getAll(String medicalRecordId);
}

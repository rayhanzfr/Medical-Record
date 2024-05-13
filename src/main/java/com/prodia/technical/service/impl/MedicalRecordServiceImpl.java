package com.prodia.technical.service.impl;

import com.prodia.technical.common.helper.SpecificationHelper;
import com.prodia.technical.common.helper.error.ErrorMessageConstant;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.validation.ValidationHelper;
import com.prodia.technical.model.request.create.CreateDiagnoseRequest;
import com.prodia.technical.model.request.create.CreateMedicalRecordRequest;
import com.prodia.technical.model.request.create.CreateRecipeMedicineRequest;
import com.prodia.technical.model.response.MedicalRecordResponse;
import com.prodia.technical.persistence.entity.MedicalRecord;
import com.prodia.technical.persistence.repository.MedicalRecordRepository;
import com.prodia.technical.service.DiagnoseService;
import com.prodia.technical.service.MedicalRecordService;
import com.prodia.technical.service.PatientService;
import com.prodia.technical.service.RecipeMedicineService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

  private final MedicalRecordRepository repository;
  private final ValidationHelper validationHelper;

  @Setter(onMethod_ = @Autowired,onParam_ = @Lazy)
  private PatientService patientService;
  @Setter(onMethod_ = @Autowired,onParam_ = @Lazy)
  private DiagnoseService diagnoseService;
  @Setter(onMethod_ = @Autowired,onParam_ = @Lazy)
  private RecipeMedicineService recipeMedicineService;

  @Override
  public void add(CreateMedicalRecordRequest request) {
    validationHelper.validate(request);
    MedicalRecord entity = new MedicalRecord();
    mapToEntity(request,entity);
    repository.saveAndFlush(entity);
    addDetail(request,entity);
  }

  private void mapToEntity(CreateMedicalRecordRequest request,MedicalRecord entity){
    patientService.getEntity(request.getPatient().getName(),request.getPatient().getEmail())
        .ifPresentOrElse(
        entity::setPatient,
        ()-> entity.setPatient(patientService.add(request.getPatient())));
    entity.setRecommendationMedic(request.getRecommendationMedic());
    entity.setIsActive(true);
  }

  private void addDetail(CreateMedicalRecordRequest request, MedicalRecord entity){
    if (request.getDiagnoses()!=null && !request.getDiagnoses().isEmpty()){
      for (CreateDiagnoseRequest diagnoseRequest: request.getDiagnoses()){
        diagnoseRequest.setMedicalRecordId(entity.getId());
        diagnoseService.add(diagnoseRequest);
      }
    }
    if (request.getRecipeMedicines()!=null && !request.getRecipeMedicines().isEmpty()){
      for (CreateRecipeMedicineRequest recipeMedicineRequest: request.getRecipeMedicines()){
        recipeMedicineRequest.setMedicalRecordId(entity.getId());
        recipeMedicineService.add(recipeMedicineRequest);
      }
    }
  }

  @Override
  public Optional<MedicalRecord> getEntityById(String id) {
    return repository.findById(id);
  }

  @Override
  public List<MedicalRecord> getAllEntity() {
    return repository.findAll();
  }

  @Override
  public Page<MedicalRecordResponse> getAll(PagingRequest pagingRequest) {
    PageRequest pageRequest = PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize(),
        SpecificationHelper.createSort(pagingRequest.getSortBy()));
    Page<MedicalRecord> pages = repository.findAll(pageRequest);
    List<MedicalRecordResponse> datas =
        pages.getContent().stream().map(this::mapToResponse).toList();

    return new PageImpl<>(datas, pageRequest, pages.getTotalElements());
  }

  @Override
  public List<MedicalRecordResponse> getAll() {
    List<MedicalRecord> list = repository.findAll();
    return list.stream().map(this::mapToResponse).toList();
  }

  @Override
  public MedicalRecordResponse getById(String id) {
    MedicalRecord entity = repository.findById(id).orElseThrow(()->
        new ResponseStatusException(HttpStatus.BAD_REQUEST,"medical record "+ErrorMessageConstant.NOT_FOUND));
    return mapToResponse(entity);
  }

  private MedicalRecordResponse mapToResponse(MedicalRecord entity){
    MedicalRecordResponse res = new MedicalRecordResponse();
    res.setId(entity.getId());
    res.setVersion(entity.getVersion());
    res.setUpdatedAt(entity.getUpdatedAt());
    res.setUpdatedBy(entity.getUpdatedBy());

    res.setPatient(patientService.get(entity.getPatient().getId()));
    res.setRecommendationMedic(entity.getRecommendationMedic());
    res.setDiagnoses(diagnoseService.getAll(entity.getId()));
    res.setRecipeMedicines(recipeMedicineService.getAll(entity.getId()));
    return res;
  }
}

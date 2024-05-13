package com.prodia.technical.service.impl;

import com.prodia.technical.common.helper.SpecificationHelper;
import com.prodia.technical.common.helper.error.ErrorMessageConstant;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.validation.ValidationHelper;
import com.prodia.technical.model.request.create.CreateDiagnoseRequest;
import com.prodia.technical.model.request.update.UpdateDiagnoseRequest;
import com.prodia.technical.model.response.DiagnoseResponse;
import com.prodia.technical.persistence.entity.Diagnose;
import com.prodia.technical.persistence.repository.DiagnoseRepository;
import com.prodia.technical.service.MedicalRecordService;
import com.prodia.technical.service.DiagnoseService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DiagnoseServiceImpl implements DiagnoseService {

  private final DiagnoseRepository repository;
  private final ValidationHelper validationHelper;
  @Setter(onMethod_ = @Autowired, onParam_ = @Lazy)
  private MedicalRecordService medicalRecordService;

  @Override
  @Transactional
  public void add(CreateDiagnoseRequest request) {
    validationHelper.validate(request);
    validateBkNotExists(request);
    Diagnose entity = new Diagnose();
    mapToEntity(request,entity);
    repository.saveAndFlush(entity);
  }

  private void validateBkNotExists(CreateDiagnoseRequest request){
    if (repository.existsByMedicalRecordIdAndCode(request.getMedicalRecordId(),
        request.getCode())){
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,"diagnose "+ ErrorMessageConstant.IS_EXISTS);
    }
  }

  @Override
  @Transactional
  public void edit(UpdateDiagnoseRequest request) {
    validationHelper.validate(request);
    getEntityById(request.getId()).ifPresentOrElse(entity->{
      validateBkNotChange(request,entity);
      mapToEntity(request,entity);
      repository.saveAndFlush(entity);
    },()->{
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"diagnose "+ ErrorMessageConstant.IS_NOT_FOUND);
    });
  }

  private void validateBkNotChange(UpdateDiagnoseRequest request,Diagnose entity){
    if (!request.getMedicalRecordId().equals(entity.getMedicalRecord().getId())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"medical record "+ErrorMessageConstant.CANNOT_BE_CHANGE);
    }
    if (!request.getCode().equals(entity.getCode())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"code "+ErrorMessageConstant.CANNOT_BE_CHANGE);
    }
  }
  private void mapToEntity(CreateDiagnoseRequest request, Diagnose entity){
    entity.setMedicalRecord(medicalRecordService.getEntityById(request.getMedicalRecordId()).orElse(null));
    entity.setCode(request.getCode());
    entity.setDescription(request.getDescription());
    entity.setIsActive(true);
  }

  @Override
  public Optional<Diagnose> getEntityById(String id) {
    return repository.findById(id);
  }

  @Override
  public List<Diagnose> getAllEntity() {
    return repository.findAll();
  }

  @Override
  public Page<DiagnoseResponse> getAll(PagingRequest pagingRequest) {
    PageRequest pageRequest = PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize(),
        SpecificationHelper.createSort(pagingRequest.getSortBy()));
    Page<Diagnose> pages = repository.findAll(pageRequest);
    List<DiagnoseResponse> datas =
        pages.getContent().stream().map(this::mapToResponse).toList();

    return new PageImpl<>(datas, pageRequest, pages.getTotalElements());
  }

  @Override
  public List<DiagnoseResponse> getAll(String medicalRecordId) {
    Specification<Diagnose> specification = Specification.where(null);
    if (medicalRecordId!=null){
      specification = specification.and(SpecificationHelper.parameterFilter("medicalRecord.id",medicalRecordId));
    }
    List<Diagnose> list = repository.findAll(specification);
    return list.stream().map(this::mapToResponse).toList();
  }

  private DiagnoseResponse mapToResponse(Diagnose entity){
    DiagnoseResponse response = new DiagnoseResponse();
    response.setId(entity.getId());
    response.setVersion(entity.getVersion());
    response.setUpdatedAt(entity.getUpdatedAt());
    response.setUpdatedBy(entity.getUpdatedBy());

    response.setMedicalRecordId(entity.getMedicalRecord().getId());
    response.setCode(entity.getCode());
    response.setDescription(entity.getDescription());
    return response;
  }
}

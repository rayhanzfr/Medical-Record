package com.prodia.technical.service.impl;

import com.prodia.technical.common.helper.SpecificationHelper;
import com.prodia.technical.common.helper.error.ErrorMessageConstant;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.validation.ValidationHelper;
import com.prodia.technical.model.request.create.CreateRecipeMedicineRequest;
import com.prodia.technical.model.request.update.UpdateRecipeMedicineRequest;
import com.prodia.technical.model.response.RecipeMedicineResponse;
import com.prodia.technical.persistence.entity.Diagnose;
import com.prodia.technical.persistence.entity.RecipeMedicine;
import com.prodia.technical.persistence.repository.RecipeMedicineRepository;
import com.prodia.technical.service.MedicalRecordService;
import com.prodia.technical.service.RecipeMedicineService;
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
public class RecipeMedicineServiceImpl implements RecipeMedicineService {

  private final RecipeMedicineRepository repository;
  private final ValidationHelper validationHelper;
  @Setter(onMethod_ = @Autowired, onParam_ = @Lazy)
  private MedicalRecordService medicalRecordService;

  @Override
  @Transactional
  public void add(CreateRecipeMedicineRequest request) {
    validationHelper.validate(request);
    validateBkNotExists(request);
    RecipeMedicine entity = new RecipeMedicine();
    mapToEntity(request,entity);
    repository.saveAndFlush(entity);
  }

  private void validateBkNotExists(CreateRecipeMedicineRequest request){
    if (repository.existsByMedicalRecordIdAndKfaCode(request.getMedicalRecordId(),
        request.getKfaCode())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"recipe medicine "+ErrorMessageConstant.IS_EXISTS);
    }
  }

  @Override
  @Transactional
  public void edit(UpdateRecipeMedicineRequest request) {
    validationHelper.validate(request);
    getEntityById(request.getId()).ifPresentOrElse(entity->{
      validateBkNotChange(request,entity);
      mapToEntity(request,entity);
      repository.saveAndFlush(entity);
    },()->{
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"recipe medicine "+ ErrorMessageConstant.IS_NOT_FOUND);
    });
  }

  private void validateBkNotChange(UpdateRecipeMedicineRequest request,RecipeMedicine entity){
    if (!request.getMedicalRecordId().equals(entity.getMedicalRecord().getId())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"medical record "+ErrorMessageConstant.CANNOT_BE_CHANGE);
    }
    if (!request.getKfaCode().equals(entity.getKfaCode())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"kfa code "+ErrorMessageConstant.CANNOT_BE_CHANGE);
    }
  }
  private void mapToEntity(CreateRecipeMedicineRequest request, RecipeMedicine entity){
    entity.setMedicalRecord(medicalRecordService.getEntityById(request.getMedicalRecordId()).orElse(null));
    entity.setName(request.getName());
    entity.setKfaCode(request.getKfaCode());
    entity.setDosePerUnit(request.getDosePerUnit());
    entity.setIsActive(true);
  }

  @Override
  public Optional<RecipeMedicine> getEntityById(String id) {
    return repository.findById(id);
  }

  @Override
  public List<RecipeMedicine> getAllEntity() {
    return repository.findAll();
  }

  @Override
  public Page<RecipeMedicineResponse> getAll(PagingRequest pagingRequest) {
    PageRequest pageRequest = PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize(),
        SpecificationHelper.createSort(pagingRequest.getSortBy()));
    Page<RecipeMedicine> pages = repository.findAll(pageRequest);
    List<RecipeMedicineResponse> datas =
        pages.getContent().stream().map(this::mapToResponse).toList();

    return new PageImpl<>(datas, pageRequest, pages.getTotalElements());
  }

  @Override
  public List<RecipeMedicineResponse> getAll(String medicalRecordId) {
    Specification<RecipeMedicine> specification = Specification.where(null);
    if (medicalRecordId!=null){
      specification = specification.and(SpecificationHelper.parameterFilter("medicalRecord.id",medicalRecordId));
    }
    List<RecipeMedicine> list = repository.findAll(specification);
    return list.stream().map(this::mapToResponse).toList();
  }

  private RecipeMedicineResponse mapToResponse(RecipeMedicine entity){
    RecipeMedicineResponse response = new RecipeMedicineResponse();
    response.setId(entity.getId());
    response.setVersion(entity.getVersion());
    response.setUpdatedAt(entity.getUpdatedAt());
    response.setUpdatedBy(entity.getUpdatedBy());

    response.setMedicalRecordId(entity.getMedicalRecord().getId());
    response.setName(entity.getName());
    response.setKfaCode(entity.getKfaCode());
    response.setDosePerUnit(entity.getDosePerUnit());
    return response;
  }
}

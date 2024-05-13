package com.prodia.technical.service.impl;

import com.prodia.technical.common.helper.SpecificationHelper;
import com.prodia.technical.common.helper.error.ErrorMessageConstant;
import com.prodia.technical.common.model.request.PagingRequest;
import com.prodia.technical.common.validation.ValidationHelper;
import com.prodia.technical.model.request.create.CreatePatientRequest;
import com.prodia.technical.model.request.update.UpdatePatientRequest;
import com.prodia.technical.model.response.PatientResponse;
import com.prodia.technical.persistence.entity.Patient;
import com.prodia.technical.persistence.repository.PatientRepository;
import com.prodia.technical.service.PatientService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

  private final PatientRepository repository;
  private final ValidationHelper validationHelper;
  private static final String MSG_PATIENT = "patient ";

  @Override
  @Transactional
  public Patient add(CreatePatientRequest request) {
    validationHelper.validate(request);
    validateBkNotExists(request);
    Patient entity = new Patient();
    mapToEntity(request,entity);
    return repository.saveAndFlush(entity);
  }

  private void validateBkNotExists(CreatePatientRequest request){
    if (repository.existsByNameAndEmail(request.getName(), request.getEmail())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MSG_PATIENT+ErrorMessageConstant.IS_EXISTS);
    }
  }

  private void mapToEntity(CreatePatientRequest request, Patient entity){
    entity.setName(request.getName());
    entity.setBirthDate(request.getBirthDate());
    entity.setEmail(request.getEmail());
    entity.setPhone(request.getPhone());
    entity.setIsActive(true);
  }

  @Override
  @Transactional
  public void edit(UpdatePatientRequest request) {
    validationHelper.validate(request);
    getEntityById(request.getId()).ifPresentOrElse(entity->{
      validateBkNotChange(request,entity);
      mapToEntity(request,entity);
      repository.saveAndFlush(entity);
    },()->{
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,MSG_PATIENT+ErrorMessageConstant.IS_NOT_FOUND);
    });
  }

  private void validateBkNotChange(UpdatePatientRequest request, Patient entity){
    if (!request.getName().equals(entity.getName())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"name "+ErrorMessageConstant.CANNOT_BE_CHANGE);
    }
    if (!request.getEmail().equals(entity.getEmail())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"email "+ErrorMessageConstant.CANNOT_BE_CHANGE);
    }
  }

  @Override
  public Optional<Patient> getEntityById(String id) {
    return repository.findById(id);
  }

  @Override
  public Optional<Patient> getEntity(String name, String email) {
    return repository.findByNameAndEmail(name, email);
  }

  @Override
  public List<Patient> getAllEntity() {
    return repository.findAll();
  }

  @Override
  public Page<PatientResponse> getAll(PagingRequest pagingRequest) {
    PageRequest pageRequest = PageRequest.of(pagingRequest.getPage(), pagingRequest.getPageSize(),
        SpecificationHelper.createSort(pagingRequest.getSortBy()));
    Page<Patient> pages = repository.findAll(pageRequest);
    List<PatientResponse> datas =
        pages.getContent().stream().map(this::mapToResponse).toList();

    return new PageImpl<>(datas, pageRequest, pages.getTotalElements());
  }

  @Override
  public List<PatientResponse> getAll() {
    List<Patient> list = repository.findAll();
    return list.stream().map(this::mapToResponse).toList();
  }

  @Override
  public PatientResponse get(String id) {
    Patient entity = repository.findById(id).orElseThrow(()->
        new ResponseStatusException(HttpStatus.BAD_REQUEST,MSG_PATIENT+ErrorMessageConstant.NOT_FOUND));
    return mapToResponse(entity);
  }

  private PatientResponse mapToResponse(Patient entity){
    PatientResponse response = new PatientResponse();
    response.setId(entity.getId());
    response.setVersion(entity.getVersion());
    response.setUpdatedAt(entity.getUpdatedAt());
    response.setUpdatedBy(entity.getUpdatedBy());

    response.setName(entity.getName());
    response.setBirthDate(entity.getBirthDate());
    response.setEmail(entity.getEmail());
    response.setPhone(entity.getPhone());
    return response;
  }
}

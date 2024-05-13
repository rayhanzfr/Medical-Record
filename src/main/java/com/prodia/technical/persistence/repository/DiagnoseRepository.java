package com.prodia.technical.persistence.repository;

import com.prodia.technical.persistence.entity.Diagnose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnoseRepository extends JpaRepository<Diagnose,String>,
    JpaSpecificationExecutor<Diagnose> {

  boolean existsByMedicalRecordIdAndCode(String medicalRecordId, String code);
}

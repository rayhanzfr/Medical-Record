package com.prodia.technical.persistence.repository;

import com.prodia.technical.persistence.entity.RecipeMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeMedicineRepository extends JpaRepository<RecipeMedicine,String>,
    JpaSpecificationExecutor<RecipeMedicine> {

  boolean existsByMedicalRecordIdAndKfaCode(String medicalRecordId,String kfaCode);
}

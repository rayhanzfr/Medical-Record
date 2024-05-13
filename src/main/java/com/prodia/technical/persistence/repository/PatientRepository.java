package com.prodia.technical.persistence.repository;

import com.prodia.technical.persistence.entity.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient,String> ,
    JpaSpecificationExecutor<Patient> {

  boolean existsByNameAndEmail(String name, String email);

  Optional<Patient> findByNameAndEmail(String name,String email);
}

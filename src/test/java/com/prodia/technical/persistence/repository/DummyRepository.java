package com.prodia.technical.persistence.repository;

import com.prodia.technical.persistence.entity.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyRepository extends JpaRepository<DummyEntity, String> {

}

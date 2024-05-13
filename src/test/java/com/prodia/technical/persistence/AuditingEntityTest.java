package com.prodia.technical.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prodia.technical.persistence.config.AuditorAwareImpl;
import com.prodia.technical.persistence.config.PersistenceTestConfig;
import com.prodia.technical.persistence.entity.DummyEntity;
import com.prodia.technical.persistence.repository.DummyRepository;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = {
  PersistenceTestConfig.class,
  DummyRepository.class,
})
class AuditingEntityTest {

  private final AuditorAwareImpl auditorAwareImpl = new AuditorAwareImpl();

  @Autowired
  private AuditorAware<String> auditorAware;

  @Autowired
  private DummyRepository dummyRepository;

  @Test
  @DisplayName("When get current auditor, should return implementation as current auditor")
  void testCurrentAuditor() {
    String auditor = auditorAware.getCurrentAuditor().orElse("");

    assertFalse(auditor.trim().isEmpty());
    assertEquals(auditorAwareImpl.getCurrentAuditor().orElse(""), auditor);
  }

  @Test
  @DisplayName("When create an entity, all audit properties should not null and as expected")
  void testAuditProperties() {
    // save entity to database
    DummyEntity dummyData = new DummyEntity("DUMMY_DATA");
    DummyEntity result = dummyRepository.save(dummyData);

    // assert all field are not null
    assertNotNull(result.getId());
    assertNotNull(result.getCreatedAt());
    assertNotNull(result.getCreatedBy());
    assertNotNull(result.getUpdatedAt());
    assertNotNull(result.getUpdatedBy());
    assertNotNull(result.getVersion());

    // additional assertions
    assertFalse(result.getId().trim().isEmpty());
    assertFalse(result.getId().length() < 32);
    assertEquals(auditorAware.getCurrentAuditor().orElse(""), result.getCreatedBy());
    assertEquals(auditorAware.getCurrentAuditor().orElse(""), result.getUpdatedBy());
    assertEquals(result.getCreatedAt(), result.getUpdatedAt());
    assertEquals(0, result.getVersion());

    ZonedDateTime currentDateTime = ZonedDateTime.now();
    assertTrue(result.getCreatedAt().isBefore(currentDateTime));
    assertTrue(result.getUpdatedAt().isBefore(currentDateTime));
  }

  @Test
  @DisplayName("When update entity, should only change updated date")
  void testAuditPropertiesWhenUpdateEntity() {
    // save entity to database
    String initialData = "DUMMY_DATA";
    DummyEntity entity = new DummyEntity(initialData);
    dummyRepository.save(entity);
    assertNotNull(entity.getId());
    assertEquals(0, entity.getVersion());

    // update data
    entity.setData("CHANGED_DUMMY_DATA");
    dummyRepository.saveAndFlush(entity);

    // assert auditing properties
    assertEquals(entity.getCreatedBy(), entity.getUpdatedBy());
    assertNotEquals(initialData, entity.getData());
    assertNotEquals(entity.getCreatedAt(), entity.getUpdatedAt());
    assertEquals(1, entity.getVersion());
    assertTrue(entity.getUpdatedAt().isAfter(entity.getCreatedAt()));
  }
}
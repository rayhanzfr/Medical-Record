//package com.prodia.technical;
//
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.prodia.technical.controller.MedicalRecordController;
//import com.prodia.technical.model.request.create.CreateDiagnoseRequest;
//import com.prodia.technical.model.request.create.CreateMedicalRecordRequest;
//import com.prodia.technical.model.request.create.CreatePatientRequest;
//import com.prodia.technical.model.request.create.CreateRecipeMedicineRequest;
//import com.prodia.technical.persistence.entity.MedicalRecord;
//import com.prodia.technical.persistence.entity.Patient;
//import com.prodia.technical.persistence.repository.MedicalRecordRepository;
//import com.prodia.technical.persistence.repository.PatientRepository;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//@WebMvcTest(MedicalRecordController.class)
//public class MedicalRecordControllerTests {
//  @MockBean
//  private MedicalRecordRepository medicalRecordRepository;
//
//  @MockBean
//  private PatientRepository patientRepository;
//  @Autowired
//  private MockMvc mockMvc;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  @Test
//  void shouldCreateTutorial() throws Exception {
//    CreatePatientRequest patientRequest = new CreatePatientRequest("Dummy",LocalDate.now(),"dummy@email.com","081234567890");
//    List<CreateDiagnoseRequest> diagnoseRequests = new ArrayList<>();
//    diagnoseRequests.add(new CreateDiagnoseRequest(null,"Code","Description"));
//    List<CreateRecipeMedicineRequest> medicineRequests = new ArrayList<>();
//    medicineRequests.add(new CreateRecipeMedicineRequest(null,"paracetamol","paracetamol",1));
//
//    CreateMedicalRecordRequest request = new CreateMedicalRecordRequest(patientRequest,"recommendation medic",diagnoseRequests,medicineRequests);
//    mockMvc.perform(post("/api/v1/medical-records").contentType(MediaType.APPLICATION_JSON)
//        .content(objectMapper.writeValueAsString(request)))
//        .andExpect(status().isCreated())
//        .andDo(print());
//  }
//
//  @Test
//  void shouldReturnNotFoundTutorial() throws Exception {
//    String id = "jkashkjhaskjhjakshkjas";
//
//    when(medicalRecordRepository.findById(id)).thenReturn(Optional.empty());
//    mockMvc.perform(get("/api/medical-records/{id}", id))
//         .andExpect(status().isNotFound())
//         .andDo(print());
//  }
//}
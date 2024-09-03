package com.medic_manager.app.controllers;

import com.medic_manager.app.mappers.PatientMapper;
import com.medic_manager.app.services.PatientService;
import com.medic_manager.app.tos.PatientTo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/com/medic-manager/app/")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(final PatientService service, final PatientMapper mapper) {
        this.patientService = service;
        this.patientMapper = mapper;
    }

    @PostMapping("create-patient")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientTo createPatient(@RequestBody @Validated PatientTo patientTo) {
        return patientMapper.toPatientTo(patientService.createPatient(patientTo));
    }

    @GetMapping("patients")
    @ResponseStatus(HttpStatus.OK)
    public List<PatientTo> getAllPatients() {
        return patientService.getAllPatients()
                .stream()
                .map(patientMapper::toPatientTo)
                .toList();
    }

    @GetMapping("patient/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PatientTo getPatientById(@PathVariable Long id) {
        return patientMapper.toPatientTo(patientService.getPatientById(id));
    }

    @PutMapping("update-patient")
    @ResponseStatus(HttpStatus.OK)
    public PatientTo updatePatient(@RequestBody @Validated PatientTo patientTo) {
        return patientMapper.toPatientTo(patientService.updatePatient(patientTo));
    }

    @DeleteMapping("delete-patient/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }
}

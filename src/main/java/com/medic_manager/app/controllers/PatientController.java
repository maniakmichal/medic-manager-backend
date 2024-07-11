package com.medic_manager.app.controllers;

import com.medic_manager.app.mappers.PatientMapper;
import com.medic_manager.app.services.PatientService;
import com.medic_manager.app.tos.PatientTo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/com/medic-manager/app/")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(final PatientService service, PatientMapper mapper) {
        this.patientService = service;
        this.patientMapper = mapper;
    }

    @GetMapping("patients")
    @ResponseStatus(HttpStatus.OK)
    public List<PatientTo> getAllPatients() {
        return patientService.getAllPatients()
                .stream()
                .map(patientMapper::toPatientTo)
                .toList();
    }
}

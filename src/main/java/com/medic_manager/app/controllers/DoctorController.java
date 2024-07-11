package com.medic_manager.app.controllers;

import com.medic_manager.app.mappers.DoctorMapper;
import com.medic_manager.app.services.DoctorService;
import com.medic_manager.app.tos.DoctorTo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/com/medic-manager/app/")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    public DoctorController(final DoctorService service, final DoctorMapper mapper) {
        this.doctorService = service;
        this.doctorMapper = mapper;
    }

    @GetMapping("doctors")
    @ResponseStatus(HttpStatus.OK)
    public List<DoctorTo> getAllDoctors() {
        return doctorService.getAllDoctors()
                .stream()
                .map(doctorMapper::toDoctorTo)
                .toList();
    }
}

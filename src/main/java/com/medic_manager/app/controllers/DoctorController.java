package com.medic_manager.app.controllers;

import com.medic_manager.app.mappers.DoctorMapper;
import com.medic_manager.app.services.DoctorService;
import com.medic_manager.app.tos.DoctorTo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("createDoctor")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorTo createDoctor(@RequestBody @Validated DoctorTo doctorTo) {
        return doctorMapper.toDoctorTo(doctorService.createDoctor(doctorTo));
    }

    @GetMapping("doctors")
    @ResponseStatus(HttpStatus.OK)
    public List<DoctorTo> getAllDoctors() {
        return doctorService.getAllDoctors()
                .stream()
                .map(doctorMapper::toDoctorTo)
                .toList();
    }

    @GetMapping("doctor/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorTo getDoctorById(@PathVariable Long id) {
        return doctorMapper.toDoctorTo(doctorService.getDoctorById(id));
    }

    @PutMapping("updateDoctor")
    @ResponseStatus(HttpStatus.OK)
    public DoctorTo updateDoctor(@RequestBody @Validated DoctorTo doctorTo) {
        return doctorMapper.toDoctorTo(doctorService.updateDoctor(doctorTo));
    }
}

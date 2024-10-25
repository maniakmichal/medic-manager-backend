package com.medic_manager.app.controllers;

import com.medic_manager.app.mappers.AppointmentMapper;
import com.medic_manager.app.services.AppointmentService;
import com.medic_manager.app.tos.AppointmentTo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/com/medic-manager/app/")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    public AppointmentController(AppointmentService appointmentService, AppointmentMapper appointmentMapper) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
    }

    @PostMapping("create-appointment")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentTo createAppointment(@RequestBody @Validated AppointmentTo appointmentTo) {
        return appointmentMapper.toAppointmentTo(appointmentService.createAppointment(appointmentTo));
    }

    @GetMapping("appointments")
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentTo> getAllAppointments() {
        return appointmentService.getAllAppointments()
                .stream()
                .map(appointmentMapper::toAppointmentTo)
                .toList();
    }

    @GetMapping("appointment/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentTo getAppointmentById(@PathVariable Long id) {
        return appointmentMapper.toAppointmentTo(appointmentService.getAppointmentById(id));
    }

    @PutMapping("update-appointment")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentTo updateAppointment(@RequestBody @Validated AppointmentTo appointmentTo) {
        return appointmentMapper.toAppointmentTo(appointmentService.updateAppointment(appointmentTo));
    }

    @DeleteMapping("delete-appointment/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
    }
}

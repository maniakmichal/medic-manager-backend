package com.medic_manager.app.controllers;

import com.medic_manager.app.mappers.DutyMapper;
import com.medic_manager.app.services.DutyService;
import com.medic_manager.app.tos.DutyTo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/com/medic-manager/app/")
public class DutyController {

    private final DutyService dutyService;
    private final DutyMapper dutyMapper;

    public DutyController(DutyService dutyService, DutyMapper dutyMapper) {
        this.dutyService = dutyService;
        this.dutyMapper = dutyMapper;
    }

    @PostMapping("create-duty")
    @ResponseStatus(HttpStatus.CREATED)
    public DutyTo createDuty(@RequestBody @Validated DutyTo dutyTo) {
        return dutyMapper.toDutyTo(dutyService.createDuty(dutyTo));
    }

    @GetMapping("duties")
    @ResponseStatus(HttpStatus.OK)
    public List<DutyTo> getAllDuties() {
        return dutyService.getAllDuties()
                .stream()
                .map(dutyMapper::toDutyTo)
                .toList();
    }

    @GetMapping("duty/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DutyTo getDutyById(@PathVariable Long id) {
        return dutyMapper.toDutyTo(dutyService.getDutyById(id));
    }

    @PutMapping("update-duty")
    @ResponseStatus(HttpStatus.OK)
    public DutyTo updateDuty(@RequestBody @Validated DutyTo dutyTo) {
        return dutyMapper.toDutyTo(dutyService.updateDuty(dutyTo));
    }

    @DeleteMapping("delete-duty/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDuty(@PathVariable Long id) {
        dutyService.deleteDuty(id);
    }
}

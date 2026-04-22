package com.tracker.client;

import com.tracker.domain.Patient;
import com.tracker.dto.PatientRequest;
import com.tracker.manager.PatientManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientManager patientManager;

    public PatientController(PatientManager patientManager) {
        this.patientManager = patientManager;
    }

    @GetMapping
    public List<Patient> listAll() {
        return patientManager.listAll();
    }

    @GetMapping("/{id}")
    public Patient getById(@PathVariable Long id) {
        return patientManager.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@RequestBody PatientRequest request) {
        return patientManager.create(request);
    }
}

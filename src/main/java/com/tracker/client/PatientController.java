package com.tracker.client;

import com.tracker.domain.Patient;
import com.tracker.dto.PatientRequest;
import com.tracker.manager.PatientManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Client layer — HTTP only, zero business logic.
 * Delegates entirely to PatientManager.
 * F1: Patient management.
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientManager patientManager;

    public PatientController(PatientManager patientManager) {
        this.patientManager = patientManager;
    }

    /** GET /api/patients — List all patients. */
    @GetMapping
    public List<Patient> listAll() {
        return patientManager.listAll();
    }

    /** GET /api/patients/{id} — Get a single patient. */
    @GetMapping("/{id}")
    public Patient getById(@PathVariable Long id) {
        return patientManager.findById(id);
    }

    /** POST /api/patients — Create a patient. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@RequestBody PatientRequest request) {
        return patientManager.create(request);
    }
}

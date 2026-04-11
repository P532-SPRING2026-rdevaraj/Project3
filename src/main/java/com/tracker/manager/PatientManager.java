package com.tracker.manager;

import com.tracker.domain.Patient;
import com.tracker.dto.PatientRequest;
import com.tracker.engine.command.CommandLog;
import com.tracker.engine.command.CreatePatientCommand;
import com.tracker.resourceaccess.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manager layer — orchestrates patient use-case sequences (F1).
 *
 * Delegates persistence to PatientRepository and wraps every state-change
 * in a Command object that is logged via CommandLog.
 */
@Service
public class PatientManager {

    private final PatientRepository patientRepository;
    private final CommandLog commandLog;

    public PatientManager(PatientRepository patientRepository, CommandLog commandLog) {
        this.patientRepository = patientRepository;
        this.commandLog = commandLog;
    }

    /** Returns all patients (F1). */
    public List<Patient> listAll() {
        return patientRepository.findAll();
    }

    /** Finds a patient by ID or throws (used internally). */
    public Patient findById(Long id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + id));
    }

    /** Creates and persists a new patient, logging the command (F1). */
    public Patient create(PatientRequest request) {
        CreatePatientCommand cmd = new CreatePatientCommand(
            patientRepository,
            request.getFullName(),
            request.getDateOfBirth(),
            request.getNote()
        );
        commandLog.execute(cmd);
        return cmd.getCreatedPatient();
    }
}

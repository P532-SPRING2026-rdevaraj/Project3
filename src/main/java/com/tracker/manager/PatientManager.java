package com.tracker.manager;

import com.tracker.domain.Patient;
import com.tracker.dto.PatientRequest;
import com.tracker.engine.command.CommandLog;
import com.tracker.engine.command.CreatePatientCommand;
import com.tracker.resourceaccess.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientManager {

    private final PatientRepository patientRepository;
    private final CommandLog commandLog;

    public PatientManager(PatientRepository patientRepository, CommandLog commandLog) {
        this.patientRepository = patientRepository;
        this.commandLog = commandLog;
    }

    public List<Patient> listAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + id));
    }

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

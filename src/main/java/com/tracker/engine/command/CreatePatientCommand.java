package com.tracker.engine.command;

import com.tracker.domain.Patient;
import com.tracker.resourceaccess.PatientRepository;

import java.time.LocalDate;

/**
 * Command pattern — wraps the "create patient" operation.
 * F1: Patient management.
 */
public class CreatePatientCommand implements Command {

    private final PatientRepository patientRepository;
    private final String fullName;
    private final LocalDate dateOfBirth;
    private final String note;

    private Patient createdPatient;

    public CreatePatientCommand(PatientRepository patientRepository,
                                String fullName, LocalDate dateOfBirth, String note) {
        this.patientRepository = patientRepository;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.note = note;
    }

    @Override
    public void execute() {
        Patient patient = new Patient(fullName, dateOfBirth, note);
        createdPatient = patientRepository.save(patient);
    }

    @Override
    public String getCommandType() {
        return "CREATE_PATIENT";
    }

    @Override
    public String getPayload() {
        return "{\"fullName\":\"" + escapeJson(fullName)
            + "\",\"dateOfBirth\":\"" + dateOfBirth
            + "\",\"note\":\"" + escapeJson(note == null ? "" : note) + "\"}";
    }

    public Patient getCreatedPatient() {
        return createdPatient;
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

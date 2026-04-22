package com.tracker.dto;

import java.time.LocalDate;

public class PatientRequest {
    private String fullName;
    private LocalDate dateOfBirth;
    private String note;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

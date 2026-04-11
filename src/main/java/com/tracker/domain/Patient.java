package com.tracker.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Operational-level entity representing a patient in the system.
 * F1: Patient management.
 */
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(columnDefinition = "TEXT")
    private String note;

    public Patient() {}

    public Patient(String fullName, LocalDate dateOfBirth, String note) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.note = note;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

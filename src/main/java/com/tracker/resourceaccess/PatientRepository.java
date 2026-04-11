package com.tracker.resourceaccess;

import com.tracker.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ResourceAccess layer — atomic business verbs for Patient persistence.
 * No SQL in callers; callers use the Spring Data method names.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}

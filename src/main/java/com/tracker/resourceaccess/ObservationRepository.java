package com.tracker.resourceaccess;

import com.tracker.domain.Observation;
import com.tracker.domain.ObservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ResourceAccess layer — atomic business verbs for Observation persistence.
 * Provides named query methods so no SQL leaks into callers.
 */
@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {

    /** Returns all observations for a patient ordered newest first (F7). */
    List<Observation> findByPatientIdOrderByRecordingTimeDesc(Long patientId);

    /** Returns only ACTIVE observations for a patient — used by rule evaluation (F6). */
    List<Observation> findByPatientIdAndStatus(Long patientId, ObservationStatus status);
}

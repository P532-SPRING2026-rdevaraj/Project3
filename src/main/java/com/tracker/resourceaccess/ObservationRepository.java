package com.tracker.resourceaccess;

import com.tracker.domain.Observation;
import com.tracker.domain.ObservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {

    List<Observation> findByPatientIdOrderByRecordingTimeDesc(Long patientId);

    List<Observation> findByPatientIdAndStatus(Long patientId, ObservationStatus status);
}

package com.tracker.manager;

import com.tracker.domain.*;
import com.tracker.dto.CategoryObservationRequest;
import com.tracker.dto.MeasurementRequest;
import com.tracker.dto.RejectObservationRequest;
import com.tracker.engine.ObservationFactory;
import com.tracker.engine.command.CommandLog;
import com.tracker.engine.command.RecordObservationCommand;
import com.tracker.engine.command.RejectObservationCommand;
import com.tracker.engine.decorator.ObservationProcessor;
import com.tracker.event.ObservationEvent;
import com.tracker.resourceaccess.ObservationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manager layer — orchestrates observation use-cases (F3, F4, F7, F8).
 *
 * Publishes ObservationEvents via Spring's ApplicationEventPublisher so
 * AuditLogListener and RuleEvaluationListener react automatically (Observer).
 *
 * Delegates object creation exclusively to ObservationFactory (Factory).
 * Wraps every state change in a Command object logged via CommandLog (Command).
 * Applies the ObservationProcessor pipeline after creation (Decorator).
 */
@Service
public class ObservationManager {

    private final ObservationRepository observationRepository;
    private final ObservationFactory observationFactory;
    private final CommandLog commandLog;
    private final ApplicationEventPublisher eventPublisher;
    private final ObservationProcessor observationProcessor;
    private final PatientManager patientManager;
    private final PhenomenonTypeManager phenomenonTypeManager;
    private final ProtocolManager protocolManager;

    public ObservationManager(ObservationRepository observationRepository,
                               ObservationFactory observationFactory,
                               CommandLog commandLog,
                               ApplicationEventPublisher eventPublisher,
                               ObservationProcessor observationProcessor,
                               PatientManager patientManager,
                               PhenomenonTypeManager phenomenonTypeManager,
                               ProtocolManager protocolManager) {
        this.observationRepository = observationRepository;
        this.observationFactory = observationFactory;
        this.commandLog = commandLog;
        this.eventPublisher = eventPublisher;
        this.observationProcessor = observationProcessor;
        this.patientManager = patientManager;
        this.phenomenonTypeManager = phenomenonTypeManager;
        this.protocolManager = protocolManager;
    }

    /** Lists all observations for a patient in reverse-chronological order (F7). */
    public List<Observation> listForPatient(Long patientId) {
        return observationRepository.findByPatientIdOrderByRecordingTimeDesc(patientId);
    }

    /** Records a quantitative measurement (F3). */
    public Observation recordMeasurement(MeasurementRequest request) {
        Patient patient = patientManager.findById(request.getPatientId());
        PhenomenonType phenomenonType = phenomenonTypeManager.findById(request.getPhenomenonTypeId());
        Protocol protocol = request.getProtocolId() != null
            ? protocolManager.findById(request.getProtocolId()) : null;

        // Factory validates and constructs — manager trusts the result
        Measurement measurement = observationFactory.createMeasurement(
            patient, phenomenonType, request.getAmount(), request.getUnit(),
            protocol, request.getApplicabilityTime()
        );

        // Decorator pipeline (no-op in Week 1; extended in Week 2)
        Observation processed = observationProcessor.process(measurement);

        String payload = buildMeasurementPayload(request);
        RecordObservationCommand cmd = new RecordObservationCommand(
            observationRepository, processed, payload);
        commandLog.execute(cmd);

        Observation saved = cmd.getSavedObservation();
        eventPublisher.publishEvent(new ObservationEvent(this, saved, ObservationEvent.Type.CREATED));
        return saved;
    }

    /** Records a qualitative category observation (F4). */
    public Observation recordCategoryObservation(CategoryObservationRequest request) {
        Patient patient = patientManager.findById(request.getPatientId());
        Phenomenon phenomenon = phenomenonTypeManager.findPhenomenonById(request.getPhenomenonId());
        Protocol protocol = request.getProtocolId() != null
            ? protocolManager.findById(request.getProtocolId()) : null;

        // Factory validates and constructs
        CategoryObservation catObs = observationFactory.createCategoryObservation(
            patient, phenomenon, request.getPresence(), protocol, request.getApplicabilityTime()
        );

        // Decorator pipeline
        Observation processed = observationProcessor.process(catObs);

        String payload = buildCategoryPayload(request);
        RecordObservationCommand cmd = new RecordObservationCommand(
            observationRepository, processed, payload);
        commandLog.execute(cmd);

        Observation saved = cmd.getSavedObservation();
        eventPublisher.publishEvent(new ObservationEvent(this, saved, ObservationEvent.Type.CREATED));
        return saved;
    }

    /** Marks an observation as rejected (F8). */
    public Observation reject(Long observationId, RejectObservationRequest request) {
        Observation obs = observationRepository.findById(observationId)
            .orElseThrow(() -> new IllegalArgumentException("Observation not found: " + observationId));

        if (!obs.isActive()) {
            throw new IllegalStateException("Observation is already rejected");
        }

        RejectObservationCommand cmd = new RejectObservationCommand(
            observationRepository, obs, request.getReason());
        commandLog.execute(cmd);

        eventPublisher.publishEvent(new ObservationEvent(this, obs, ObservationEvent.Type.REJECTED));
        return obs;
    }

    // --- helpers ---------------------------------------------------------------

    private String buildMeasurementPayload(MeasurementRequest r) {
        return "{\"patientId\":" + r.getPatientId()
            + ",\"phenomenonTypeId\":" + r.getPhenomenonTypeId()
            + ",\"amount\":" + r.getAmount()
            + ",\"unit\":\"" + r.getUnit() + "\""
            + (r.getProtocolId() != null ? ",\"protocolId\":" + r.getProtocolId() : "")
            + "}";
    }

    private String buildCategoryPayload(CategoryObservationRequest r) {
        return "{\"patientId\":" + r.getPatientId()
            + ",\"phenomenonId\":" + r.getPhenomenonId()
            + ",\"presence\":\"" + r.getPresence() + "\""
            + (r.getProtocolId() != null ? ",\"protocolId\":" + r.getProtocolId() : "")
            + "}";
    }
}

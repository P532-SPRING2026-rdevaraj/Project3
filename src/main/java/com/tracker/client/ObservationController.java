package com.tracker.client;

import com.tracker.domain.Observation;
import com.tracker.domain.PhenomenonType;
import com.tracker.dto.*;
import com.tracker.manager.DiagnosticRuleManager;
import com.tracker.manager.ObservationManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Client layer — HTTP only, zero business logic.
 * Covers F3, F4, F6, F7, F8.
 */
@RestController
public class ObservationController {

    private final ObservationManager observationManager;
    private final DiagnosticRuleManager diagnosticRuleManager;

    public ObservationController(ObservationManager observationManager,
                                  DiagnosticRuleManager diagnosticRuleManager) {
        this.observationManager = observationManager;
        this.diagnosticRuleManager = diagnosticRuleManager;
    }

    /** GET /api/patients/{id}/observations — List observations (F7). */
    @GetMapping("/api/patients/{id}/observations")
    public List<ObservationResponse> listForPatient(@PathVariable Long id) {
        return observationManager.listForPatient(id).stream()
            .map(ObservationResponse::from)
            .toList();
    }

    /** POST /api/observations/measurement — Record measurement (F3). */
    @PostMapping("/api/observations/measurement")
    @ResponseStatus(HttpStatus.CREATED)
    public ObservationResponse recordMeasurement(@RequestBody MeasurementRequest request) {
        Observation obs = observationManager.recordMeasurement(request);
        return ObservationResponse.from(obs);
    }

    /** POST /api/observations/category — Record category observation (F4). */
    @PostMapping("/api/observations/category")
    @ResponseStatus(HttpStatus.CREATED)
    public ObservationResponse recordCategory(@RequestBody CategoryObservationRequest request) {
        Observation obs = observationManager.recordCategoryObservation(request);
        return ObservationResponse.from(obs);
    }

    /** POST /api/observations/{id}/reject — Reject observation (F8). */
    @PostMapping("/api/observations/{id}/reject")
    public ObservationResponse reject(@PathVariable Long id,
                                      @RequestBody RejectObservationRequest request) {
        Observation obs = observationManager.reject(id, request);
        return ObservationResponse.from(obs);
    }

    /** POST /api/patients/{id}/evaluate — Run diagnostic rules (F6). */
    @PostMapping("/api/patients/{id}/evaluate")
    public List<String> evaluate(@PathVariable Long id) {
        List<PhenomenonType> inferred = diagnosticRuleManager.evaluateForPatient(id);
        return inferred.stream().map(PhenomenonType::getName).toList();
    }
}

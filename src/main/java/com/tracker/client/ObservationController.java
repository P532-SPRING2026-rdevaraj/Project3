package com.tracker.client;

import com.tracker.domain.Observation;
import com.tracker.dto.*;
import com.tracker.manager.DiagnosticRuleManager;
import com.tracker.manager.ObservationManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ObservationController {

    private final ObservationManager observationManager;
    private final DiagnosticRuleManager diagnosticRuleManager;

    public ObservationController(ObservationManager observationManager,
                                  DiagnosticRuleManager diagnosticRuleManager) {
        this.observationManager = observationManager;
        this.diagnosticRuleManager = diagnosticRuleManager;
    }

    @GetMapping("/api/patients/{id}/observations")
    public List<ObservationResponse> listForPatient(@PathVariable Long id) {
        return observationManager.listForPatient(id).stream()
            .map(ObservationResponse::from)
            .toList();
    }

    @PostMapping("/api/observations/measurement")
    @ResponseStatus(HttpStatus.CREATED)
    public ObservationResponse recordMeasurement(@RequestBody MeasurementRequest request) {
        Observation obs = observationManager.recordMeasurement(request);
        return ObservationResponse.from(obs);
    }

    @PostMapping("/api/observations/category")
    @ResponseStatus(HttpStatus.CREATED)
    public ObservationResponse recordCategory(@RequestBody CategoryObservationRequest request) {
        Observation obs = observationManager.recordCategoryObservation(request);
        return ObservationResponse.from(obs);
    }

    @PostMapping("/api/observations/{id}/reject")
    public ObservationResponse reject(@PathVariable Long id,
                                      @RequestBody RejectObservationRequest request) {
        Observation obs = observationManager.reject(id, request);
        return ObservationResponse.from(obs);
    }

    @PostMapping("/api/patients/{id}/evaluate")
    public List<EvaluationResult> evaluate(@PathVariable Long id) {
        return diagnosticRuleManager.evaluateForPatient(id);
    }
}

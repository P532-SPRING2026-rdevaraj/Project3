package com.tracker.manager;

import com.tracker.domain.*;
import com.tracker.dto.AssociativeFunctionRequest;
import com.tracker.engine.DiagnosisEngine;
import com.tracker.resourceaccess.AssociativeFunctionRepository;
import com.tracker.resourceaccess.ObservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manager layer — orchestrates diagnostic rule use-cases (F6).
 *
 * Delegates evaluation to DiagnosisEngine (Strategy pattern lives there).
 */
@Service
public class DiagnosticRuleManager {

    private final AssociativeFunctionRepository ruleRepository;
    private final ObservationRepository observationRepository;
    private final DiagnosisEngine diagnosisEngine;
    private final PhenomenonTypeManager phenomenonTypeManager;

    public DiagnosticRuleManager(AssociativeFunctionRepository ruleRepository,
                                  ObservationRepository observationRepository,
                                  DiagnosisEngine diagnosisEngine,
                                  PhenomenonTypeManager phenomenonTypeManager) {
        this.ruleRepository = ruleRepository;
        this.observationRepository = observationRepository;
        this.diagnosisEngine = diagnosisEngine;
        this.phenomenonTypeManager = phenomenonTypeManager;
    }

    public List<AssociativeFunction> listAll() {
        return ruleRepository.findAll();
    }

    public AssociativeFunction create(AssociativeFunctionRequest request) {
        PhenomenonType productConcept = phenomenonTypeManager.findById(request.getProductConceptId());
        AssociativeFunction rule = new AssociativeFunction(
            request.getName(), request.getArgumentConceptIds(), productConcept);
        return ruleRepository.save(rule);
    }

    /**
     * Evaluates all active rules against a patient's ACTIVE observations (F6).
     * Inferences are returned but NOT saved as observations.
     */
    public List<PhenomenonType> evaluateForPatient(Long patientId) {
        List<Observation> activeObs = observationRepository
            .findByPatientIdAndStatus(patientId, ObservationStatus.ACTIVE);
        List<AssociativeFunction> activeRules = ruleRepository.findByActiveTrue();
        return diagnosisEngine.evaluate(activeRules, activeObs);
    }
}

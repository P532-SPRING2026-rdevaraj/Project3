package com.tracker.manager;

import com.tracker.domain.*;
import com.tracker.dto.AssociativeFunctionRequest;
import com.tracker.dto.EvaluationResult;
import com.tracker.engine.DiagnosisEngine;
import com.tracker.resourceaccess.AssociativeFunctionRepository;
import com.tracker.resourceaccess.ObservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (request.getStrategyType() != null) {
            rule.setStrategyType(request.getStrategyType());
        }
        if (request.getWeightsMap() != null) {
            rule.setWeightsMap(request.getWeightsMap());
        }
        if (request.getThreshold() != null) {
            rule.setThreshold(request.getThreshold());
        }
        return ruleRepository.save(rule);
    }

    public List<EvaluationResult> evaluateForPatient(Long patientId) {
        List<Observation> activeManualObs = observationRepository
            .findByPatientIdAndStatus(patientId, ObservationStatus.ACTIVE)
            .stream()
            .filter(o -> o.getSource() == ObservationSource.MANUAL)
            .toList();
        List<AssociativeFunction> activeRules = ruleRepository.findByActiveTrue();
        return diagnosisEngine.evaluate(activeRules, activeManualObs);
    }
}

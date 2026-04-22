package com.tracker.engine.strategy;

import com.tracker.domain.AssociativeFunction;
import com.tracker.domain.Observation;

import java.util.List;

public interface DiagnosisStrategy {

    boolean evaluate(AssociativeFunction rule, List<Observation> patientObservations);
}

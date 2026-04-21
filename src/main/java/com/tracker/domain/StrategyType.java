package com.tracker.domain;

/**
 * Selects which DiagnosisStrategy an AssociativeFunction should use when
 * evaluated by DiagnosisEngine (Change 1 — multiple diagnosis strategies).
 */
public enum StrategyType {
    CONJUNCTIVE,
    WEIGHTED
}

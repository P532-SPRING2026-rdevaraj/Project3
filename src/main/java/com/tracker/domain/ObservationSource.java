package com.tracker.domain;

/**
 * Distinguishes manually entered observations from those inferred by the
 * PropagationListener (Change 4 — concept hierarchy propagation).
 *
 * DiagnosisStrategy implementations filter on MANUAL to avoid circular
 * inference chains during rule evaluation.
 */
public enum ObservationSource {
    MANUAL,
    INFERRED
}

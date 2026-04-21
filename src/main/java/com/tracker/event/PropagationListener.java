package com.tracker.event;

import com.tracker.domain.*;
import com.tracker.engine.ObservationFactory;
import com.tracker.resourceaccess.ObservationRepository;
import com.tracker.resourceaccess.PhenomenonRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Observer pattern — Listener 3 (Change 4 — concept hierarchy propagation).
 *
 * After a CategoryObservation is saved, propagates presence/absence up/down the
 * Phenomenon hierarchy according to Analysis Patterns Section 3.6:
 *
 *   PRESENT → creates INFERRED PRESENT for all ancestor concepts not already present.
 *   ABSENT  → creates INFERRED ABSENT for all descendant concepts not already absent.
 *
 * Saves inferred observations directly via repository (not ObservationManager) to
 * avoid re-triggering this listener and creating infinite loops.
 * Inferred observations are flagged source=INFERRED and displayed in italics in the UI.
 */
@Component
public class PropagationListener {

    private final ObservationRepository observationRepository;
    private final PhenomenonRepository phenomenonRepository;
    private final ObservationFactory observationFactory;

    public PropagationListener(ObservationRepository observationRepository,
                               PhenomenonRepository phenomenonRepository,
                               ObservationFactory observationFactory) {
        this.observationRepository = observationRepository;
        this.phenomenonRepository = phenomenonRepository;
        this.observationFactory = observationFactory;
    }

    @EventListener
    public void onObservationEvent(ObservationEvent event) {
        if (event.getEventType() != ObservationEvent.Type.CREATED) return;
        Observation obs = event.getObservation();
        if (!(obs instanceof CategoryObservation catObs)) return;
        if (catObs.getSource() == ObservationSource.INFERRED) return; // avoid recursion

        Patient patient = catObs.getPatient();
        Phenomenon phenomenon = catObs.getPhenomenon();

        if (catObs.getPresence() == Presence.PRESENT) {
            propagatePresent(patient, phenomenon);
        } else {
            propagateAbsent(patient, phenomenon);
        }
    }

    /** Creates INFERRED PRESENT for all ancestors not already present. */
    private void propagatePresent(Patient patient, Phenomenon phenomenon) {
        Set<Long> alreadyPresent = getActivePresent(patient);
        List<Phenomenon> ancestors = getAncestors(phenomenon);
        for (Phenomenon ancestor : ancestors) {
            if (!alreadyPresent.contains(ancestor.getId())) {
                CategoryObservation inferred = observationFactory.createCategoryObservation(
                    patient, ancestor, Presence.PRESENT, null, null, ObservationSource.INFERRED);
                observationRepository.save(inferred);
            }
        }
    }

    /** Creates INFERRED ABSENT for all descendants not already absent. */
    private void propagateAbsent(Patient patient, Phenomenon phenomenon) {
        Set<Long> alreadyAbsent = getActiveAbsent(patient);
        List<Phenomenon> descendants = getDescendants(phenomenon);
        for (Phenomenon descendant : descendants) {
            if (!alreadyAbsent.contains(descendant.getId())) {
                CategoryObservation inferred = observationFactory.createCategoryObservation(
                    patient, descendant, Presence.ABSENT, null, null, ObservationSource.INFERRED);
                observationRepository.save(inferred);
            }
        }
    }

    /** Walks the parentConcept chain and returns all ancestors (root-most last). */
    private List<Phenomenon> getAncestors(Phenomenon phenomenon) {
        List<Phenomenon> ancestors = new ArrayList<>();
        Phenomenon current = phenomenon.getParentConcept();
        while (current != null) {
            ancestors.add(current);
            current = current.getParentConcept();
        }
        return ancestors;
    }

    /** Returns all phenomena whose parentConcept chain includes this phenomenon. */
    private List<Phenomenon> getDescendants(Phenomenon phenomenon) {
        List<Phenomenon> all = phenomenonRepository.findAll();
        return all.stream()
            .filter(p -> isDescendantOf(p, phenomenon.getId()))
            .collect(Collectors.toList());
    }

    private boolean isDescendantOf(Phenomenon p, Long ancestorId) {
        Phenomenon current = p.getParentConcept();
        while (current != null) {
            if (current.getId().equals(ancestorId)) return true;
            current = current.getParentConcept();
        }
        return false;
    }

    private Set<Long> getActivePresent(Patient patient) {
        return observationRepository.findByPatientIdAndStatus(patient.getId(), ObservationStatus.ACTIVE)
            .stream()
            .filter(o -> o instanceof CategoryObservation c && c.getPresence() == Presence.PRESENT)
            .map(o -> ((CategoryObservation) o).getPhenomenon().getId())
            .collect(Collectors.toSet());
    }

    private Set<Long> getActiveAbsent(Patient patient) {
        return observationRepository.findByPatientIdAndStatus(patient.getId(), ObservationStatus.ACTIVE)
            .stream()
            .filter(o -> o instanceof CategoryObservation c && c.getPresence() == Presence.ABSENT)
            .map(o -> ((CategoryObservation) o).getPhenomenon().getId())
            .collect(Collectors.toSet());
    }
}

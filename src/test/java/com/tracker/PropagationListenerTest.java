package com.tracker;

import com.tracker.domain.*;
import com.tracker.engine.ObservationFactory;
import com.tracker.event.ObservationEvent;
import com.tracker.event.PropagationListener;
import com.tracker.resourceaccess.ObservationRepository;
import com.tracker.resourceaccess.PhenomenonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PropagationListener (Change 4).
 * Covers PRESENT ancestor propagation and ABSENT descendant propagation.
 */
@ExtendWith(MockitoExtension.class)
class PropagationListenerTest {

    @Mock ObservationRepository observationRepository;
    @Mock PhenomenonRepository phenomenonRepository;

    private static final Clock FIXED_CLOCK =
        Clock.fixed(Instant.parse("2026-04-19T10:00:00Z"), ZoneId.of("UTC"));

    private PropagationListener listener;
    private ObservationFactory factory;

    private Patient patient;
    private PhenomenonType qualType;
    private Phenomenon grandparent, parent, child;

    @BeforeEach
    void setUp() {
        factory = new ObservationFactory(FIXED_CLOCK);
        listener = new PropagationListener(observationRepository, phenomenonRepository, factory);

        patient = new Patient("Alice", LocalDate.of(1990, 1, 1), null);
        patient.setId(1L);

        qualType = new PhenomenonType("Condition", MeasurementKind.QUALITATIVE);
        qualType.setId(10L);

        grandparent = new Phenomenon("Grandparent", qualType);
        grandparent.setId(1L);

        parent = new Phenomenon("Parent", qualType);
        parent.setId(2L);
        parent.setParentConcept(grandparent);

        child = new Phenomenon("Child", qualType);
        child.setId(3L);
        child.setParentConcept(parent);
    }

    private CategoryObservation catObs(Phenomenon ph, Presence presence, ObservationSource source) {
        CategoryObservation obs = new CategoryObservation(
            patient, Instant.now(), Instant.now(), null, ph, presence);
        obs.setSource(source);
        obs.setId((long)(Math.random() * 1000 + 100));
        return obs;
    }

    @Test
    void present_observation_propagatesPresenceToAncestors() {
        // Arrange: child is marked PRESENT, ancestors not yet present
        CategoryObservation trigger = catObs(child, Presence.PRESENT, ObservationSource.MANUAL);
        when(observationRepository.findByPatientIdAndStatus(eq(1L), eq(ObservationStatus.ACTIVE)))
            .thenReturn(List.of(trigger));
        when(observationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ObservationEvent event = new ObservationEvent(this, trigger, ObservationEvent.Type.CREATED);

        // Act
        listener.onObservationEvent(event);

        // Assert: 2 inferred PRESENT observations should be saved (parent + grandparent)
        ArgumentCaptor<Observation> captor = ArgumentCaptor.forClass(Observation.class);
        verify(observationRepository, atLeast(2)).save(captor.capture());
        long inferredCount = captor.getAllValues().stream()
            .filter(o -> o.getSource() == ObservationSource.INFERRED)
            .count();
        assertEquals(2, inferredCount, "Should infer PRESENT for parent and grandparent");
    }

    @Test
    void absent_observation_propagatesAbsenceToDescendants() {
        // Arrange: grandparent is marked ABSENT, descendants not yet absent
        CategoryObservation trigger = catObs(grandparent, Presence.ABSENT, ObservationSource.MANUAL);
        when(observationRepository.findByPatientIdAndStatus(eq(1L), eq(ObservationStatus.ACTIVE)))
            .thenReturn(List.of(trigger));
        when(phenomenonRepository.findAll()).thenReturn(List.of(grandparent, parent, child));
        when(observationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ObservationEvent event = new ObservationEvent(this, trigger, ObservationEvent.Type.CREATED);

        // Act
        listener.onObservationEvent(event);

        // Assert: 2 inferred ABSENT observations (parent + child)
        ArgumentCaptor<Observation> captor = ArgumentCaptor.forClass(Observation.class);
        verify(observationRepository, atLeast(2)).save(captor.capture());
        long inferredAbsentCount = captor.getAllValues().stream()
            .filter(o -> o.getSource() == ObservationSource.INFERRED
                && o instanceof CategoryObservation c && c.getPresence() == Presence.ABSENT)
            .count();
        assertEquals(2, inferredAbsentCount, "Should infer ABSENT for parent and child");
    }

    @Test
    void inferred_observation_doesNotTriggerFurtherPropagation() {
        // Arrange: an already-INFERRED observation should not start another propagation cycle
        CategoryObservation trigger = catObs(child, Presence.PRESENT, ObservationSource.INFERRED);
        ObservationEvent event = new ObservationEvent(this, trigger, ObservationEvent.Type.CREATED);

        // Act
        listener.onObservationEvent(event);

        // Assert: no new observations saved (short-circuit)
        verify(observationRepository, never()).save(any());
    }

    @Test
    void rejected_event_isIgnored() {
        // Arrange
        CategoryObservation trigger = catObs(child, Presence.PRESENT, ObservationSource.MANUAL);
        ObservationEvent event = new ObservationEvent(this, trigger, ObservationEvent.Type.REJECTED);

        // Act
        listener.onObservationEvent(event);

        // Assert: no propagation for REJECTED events
        verify(observationRepository, never()).save(any());
    }
}

package com.tracker.manager;

import com.tracker.domain.*;
import com.tracker.event.ObservationEvent;
import com.tracker.resourceaccess.CommandLogEntryRepository;
import com.tracker.resourceaccess.ObservationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Manager layer — handles undo of recorded or rejected observations (Change 3).
 *
 * Undo is reconstructed from the persisted CommandLogEntry rather than replaying
 * the original Command object, which keeps the Command classes free of undo logic
 * and event-publishing dependencies.
 */
@Service
public class UndoManager {

    private final CommandLogEntryRepository commandLogEntryRepository;
    private final ObservationRepository observationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UndoManager(CommandLogEntryRepository commandLogEntryRepository,
                       ObservationRepository observationRepository,
                       ApplicationEventPublisher eventPublisher) {
        this.commandLogEntryRepository = commandLogEntryRepository;
        this.observationRepository = observationRepository;
        this.eventPublisher = eventPublisher;
    }

    public CommandLogEntry undoCommand(Long entryId, String currentUser) {
        CommandLogEntry entry = commandLogEntryRepository.findById(entryId)
            .orElseThrow(() -> new IllegalArgumentException("Command log entry not found: " + entryId));

        if (entry.isUndone()) {
            throw new IllegalStateException("Command " + entryId + " has already been undone");
        }
        if (!entry.getUser().equals(currentUser)) {
            throw new SecurityException("Only the user who executed this command may undo it");
        }

        switch (entry.getCommandType()) {
            case "RECORD_OBSERVATION" -> {
                final Long obsId = resolveObservationId(entry);
                Observation obs = findObservation(obsId);
                obs.setStatus(ObservationStatus.REJECTED);
                obs.setRejectionReason("Undone by user");
                observationRepository.save(obs);
                eventPublisher.publishEvent(new ObservationEvent(this, obs, ObservationEvent.Type.REJECTED));
            }
            case "REJECT_OBSERVATION" -> {
                final Long obsId = resolveObservationId(entry);
                Observation obs = findObservation(obsId);
                obs.setStatus(ObservationStatus.ACTIVE);
                obs.setRejectionReason(null);
                observationRepository.save(obs);
                eventPublisher.publishEvent(new ObservationEvent(this, obs, ObservationEvent.Type.CREATED));
            }
            case "CREATE_PATIENT" ->
                throw new UnsupportedOperationException("Create patient cannot be undone");
            default ->
                throw new UnsupportedOperationException("Undo not supported for: " + entry.getCommandType());
        }

        entry.setUndone(true);
        return commandLogEntryRepository.save(entry);
    }

    private Long resolveObservationId(CommandLogEntry entry) {
        if (entry.getObservationId() != null) return entry.getObservationId();
        return parseObservationIdFromPayload(entry.getPayload());
    }

    private Observation findObservation(Long obsId) {
        return observationRepository.findById(obsId)
            .orElseThrow(() -> new IllegalArgumentException("Observation not found: " + obsId));
    }

    private Long parseObservationIdFromPayload(String payload) {
        if (payload == null) throw new IllegalStateException("No observationId in command payload");
        int idx = payload.indexOf("\"observationId\":");
        if (idx < 0) throw new IllegalStateException("No observationId in command payload");
        String rest = payload.substring(idx + 16).trim();
        return Long.parseLong(rest.replaceAll("[^0-9].*", ""));
    }
}

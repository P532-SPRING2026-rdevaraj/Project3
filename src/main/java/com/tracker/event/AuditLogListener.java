package com.tracker.event;

import com.tracker.domain.AuditLogEntry;
import com.tracker.resourceaccess.AuditLogEntryRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuditLogListener {

    private final AuditLogEntryRepository auditLogEntryRepository;

    public AuditLogListener(AuditLogEntryRepository auditLogEntryRepository) {
        this.auditLogEntryRepository = auditLogEntryRepository;
    }

    @EventListener
    public void onObservationEvent(ObservationEvent event) {
        String eventLabel = switch (event.getEventType()) {
            case CREATED  -> "OBSERVATION_CREATED";
            case REJECTED -> "OBSERVATION_REJECTED";
        };

        AuditLogEntry entry = new AuditLogEntry(
            eventLabel,
            event.getObservation().getId(),
            event.getObservation().getPatient().getId(),
            Instant.now(),
            null
        );
        auditLogEntryRepository.save(entry);
    }
}

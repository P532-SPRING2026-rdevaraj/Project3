package com.tracker.engine.command;

import com.tracker.domain.CommandLogEntry;
import com.tracker.engine.UserContextHolder;
import com.tracker.resourceaccess.CommandLogEntryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

/**
 * Week 2 extension of CommandLog (Change 3).
 *
 * Overrides execute() to:
 *   1. Read the acting user from UserContextHolder (set per-request by UserInterceptor).
 *   2. Persist the affected observationId in the log entry for undo path support.
 *
 * ObservationId is captured in two ways — no interface change required on commands:
 *   - RecordObservationCommand: getSavedObservation().getId() (method existed in Week 1).
 *   - All other commands: parse "observationId" from getPayload() JSON if present
 *     (RejectObservationCommand already included it in Week 1's payload).
 *
 * @Primary ensures Spring injects this everywhere CommandLog is declared.
 */
@Primary
@Service
public class AuditableCommandLog extends CommandLog {

    private final CommandLogEntryRepository repository;
    private final Clock clock;

    public AuditableCommandLog(CommandLogEntryRepository repository, Clock clock) {
        super(repository, clock);
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public void execute(Command command) {
        command.execute();

        CommandLogEntry entry = new CommandLogEntry(
            command.getCommandType(),
            command.getPayload(),
            Instant.now(clock),
            UserContextHolder.get()
        );

        // Capture observationId without requiring commands to implement a new interface.
        // RecordObservationCommand exposes the saved observation directly (Week 1 method).
        // For all other commands the observationId is already embedded in getPayload() JSON.
        if (command instanceof RecordObservationCommand roc && roc.getSavedObservation() != null) {
            entry.setObservationId(roc.getSavedObservation().getId());
        } else {
            entry.setObservationId(parseObservationIdSilently(command.getPayload()));
        }

        repository.save(entry);
    }

    private Long parseObservationIdSilently(String payload) {
        if (payload == null) return null;
        int idx = payload.indexOf("\"observationId\":");
        if (idx < 0) return null;
        try {
            String rest = payload.substring(idx + 16).trim();
            return Long.parseLong(rest.replaceAll("[^0-9].*", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

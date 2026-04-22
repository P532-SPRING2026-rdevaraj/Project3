package com.tracker.engine.command;

import com.tracker.domain.CommandLogEntry;
import com.tracker.engine.UserContextHolder;
import com.tracker.resourceaccess.CommandLogEntryRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

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

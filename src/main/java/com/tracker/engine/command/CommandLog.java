package com.tracker.engine.command;

import com.tracker.domain.CommandLogEntry;
import com.tracker.resourceaccess.CommandLogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Command pattern — stores every executed Command in the database.
 * Week 1: records user as "staff" (hard-coded).
 * Week 2 extension: AuditableCommandLog overrides execute() to read the
 * real user from UserContextHolder and persist the affected observationId.
 */
@Service
public class CommandLog {

    private final CommandLogEntryRepository repository;
    private final Clock clock;

    public CommandLog(CommandLogEntryRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public void execute(Command command) {
        command.execute();
        CommandLogEntry entry = new CommandLogEntry(
            command.getCommandType(),
            command.getPayload(),
            Instant.now(clock),
            "staff"
        );
        repository.save(entry);
    }

    public List<CommandLogEntry> getAll() {
        return repository.findAllByOrderByExecutedAtDesc();
    }
}

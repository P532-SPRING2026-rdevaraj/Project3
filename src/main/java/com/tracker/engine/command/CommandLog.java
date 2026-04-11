package com.tracker.engine.command;

import com.tracker.domain.CommandLogEntry;
import com.tracker.resourceaccess.CommandLogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Command pattern — stores every executed Command in the database.
 *
 * Managers call CommandLog.execute(command) instead of command.execute()
 * directly, ensuring every state change is automatically logged.
 */
@Service
public class CommandLog {

    private final CommandLogEntryRepository repository;
    private final Clock clock;

    private static final String CURRENT_USER = "staff";  // Week 1: single hard-coded user

    public CommandLog(CommandLogEntryRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    /**
     * Executes the command and persists a log entry with a timestamp and user.
     */
    public void execute(Command command) {
        command.execute();
        CommandLogEntry entry = new CommandLogEntry(
            command.getCommandType(),
            command.getPayload(),
            Instant.now(clock),
            CURRENT_USER
        );
        repository.save(entry);
    }

    /** Returns all log entries newest first (read-only endpoint). */
    public List<CommandLogEntry> getAll() {
        return repository.findAllByOrderByExecutedAtDesc();
    }
}

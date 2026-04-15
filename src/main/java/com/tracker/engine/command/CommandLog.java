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

    /**
     * Undoes the last executed command if it implements UndoableCommand.
     * Week 2 calls this without modifying the rest of CommandLog.
     *
     * @param command the command to undo — must implement UndoableCommand
     * @throws UnsupportedOperationException if the command is not undoable
     */
    public void undo(Command command) {
        if (command instanceof UndoableCommand undoable) {
            undoable.undo();
        } else {
            throw new UnsupportedOperationException(
                command.getCommandType() + " does not support undo");
        }
    }

    /** Returns all log entries newest first (read-only endpoint). */
    public List<CommandLogEntry> getAll() {
        return repository.findAllByOrderByExecutedAtDesc();
    }
}

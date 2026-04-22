package com.tracker.engine.command;

import com.tracker.domain.CommandLogEntry;
import com.tracker.resourceaccess.CommandLogEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

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

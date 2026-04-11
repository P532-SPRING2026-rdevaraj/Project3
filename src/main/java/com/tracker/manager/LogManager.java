package com.tracker.manager;

import com.tracker.domain.AuditLogEntry;
import com.tracker.domain.CommandLogEntry;
import com.tracker.engine.command.CommandLog;
import com.tracker.resourceaccess.AuditLogEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manager layer — provides read access to command and audit logs.
 */
@Service
public class LogManager {

    private final CommandLog commandLog;
    private final AuditLogEntryRepository auditLogEntryRepository;

    public LogManager(CommandLog commandLog, AuditLogEntryRepository auditLogEntryRepository) {
        this.commandLog = commandLog;
        this.auditLogEntryRepository = auditLogEntryRepository;
    }

    public List<CommandLogEntry> getCommandLog() {
        return commandLog.getAll();
    }

    public List<AuditLogEntry> getAuditLog() {
        return auditLogEntryRepository.findAllByOrderByTimestampDesc();
    }
}

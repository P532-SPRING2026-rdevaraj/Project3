package com.tracker.client;

import com.tracker.domain.AuditLogEntry;
import com.tracker.domain.CommandLogEntry;
import com.tracker.manager.LogManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Client layer — HTTP only, zero business logic.
 * Exposes read-only command log and audit log endpoints.
 */
@RestController
public class LogController {

    private final LogManager logManager;

    public LogController(LogManager logManager) {
        this.logManager = logManager;
    }

    /** GET /api/command-log — View command log. */
    @GetMapping("/api/command-log")
    public List<CommandLogEntry> getCommandLog() {
        return logManager.getCommandLog();
    }

    /** GET /api/audit-log — View audit log. */
    @GetMapping("/api/audit-log")
    public List<AuditLogEntry> getAuditLog() {
        return logManager.getAuditLog();
    }
}

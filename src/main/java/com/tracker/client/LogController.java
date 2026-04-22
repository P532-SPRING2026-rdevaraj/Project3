package com.tracker.client;

import com.tracker.domain.AuditLogEntry;
import com.tracker.domain.CommandLogEntry;
import com.tracker.manager.LogManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LogController {

    private final LogManager logManager;

    public LogController(LogManager logManager) {
        this.logManager = logManager;
    }

    @GetMapping("/api/command-log")
    public List<CommandLogEntry> getCommandLog() {
        return logManager.getCommandLog();
    }

    @GetMapping("/api/audit-log")
    public List<AuditLogEntry> getAuditLog() {
        return logManager.getAuditLog();
    }
}

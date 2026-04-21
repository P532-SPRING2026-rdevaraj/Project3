package com.tracker.client;

import com.tracker.domain.CommandLogEntry;
import com.tracker.engine.UserContextHolder;
import com.tracker.manager.UndoManager;
import org.springframework.web.bind.annotation.*;

/**
 * Client layer — exposes the undo endpoint for reversible commands (Change 3).
 * Separated from LogController so LogController remains a read-only log viewer
 * with zero Week 2 modifications.
 */
@RestController
public class UndoController {

    private final UndoManager undoManager;

    public UndoController(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    /** POST /api/command-log/{id}/undo — only the original user may undo their own command. */
    @PostMapping("/api/command-log/{id}/undo")
    public CommandLogEntry undoCommand(@PathVariable Long id) {
        return undoManager.undoCommand(id, UserContextHolder.get());
    }
}

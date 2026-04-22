package com.tracker.client;

import com.tracker.domain.CommandLogEntry;
import com.tracker.engine.UserContextHolder;
import com.tracker.manager.UndoManager;
import org.springframework.web.bind.annotation.*;

@RestController
public class UndoController {

    private final UndoManager undoManager;

    public UndoController(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    @PostMapping("/api/command-log/{id}/undo")
    public CommandLogEntry undoCommand(@PathVariable Long id) {
        return undoManager.undoCommand(id, UserContextHolder.get());
    }
}

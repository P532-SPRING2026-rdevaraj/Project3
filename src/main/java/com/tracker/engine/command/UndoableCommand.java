package com.tracker.engine.command;

/**
 * Command pattern extension — marks a Command as reversible.
 *
 * Week 1 commands (CreatePatientCommand, RecordObservationCommand,
 * RejectObservationCommand) do not implement this interface, so adding undo()
 * here does not force any changes to existing code.
 *
 * Week 2 commands that support undo implement this interface alongside Command.
 * CommandLog can then check: if (cmd instanceof UndoableCommand u) u.undo();
 */
public interface UndoableCommand extends Command {

    /**
     * Reverses the effect of execute().
     * Called by CommandLog when processing an undo request.
     */
    void undo();
}

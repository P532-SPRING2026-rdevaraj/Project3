package com.tracker.engine.command;

public interface UndoableCommand extends Command {

    void undo();
}

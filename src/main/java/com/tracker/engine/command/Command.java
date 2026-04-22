package com.tracker.engine.command;

public interface Command {

    void execute();

    String getCommandType();

    String getPayload();
}

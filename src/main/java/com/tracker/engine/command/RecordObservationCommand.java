package com.tracker.engine.command;

import com.tracker.domain.Observation;
import com.tracker.resourceaccess.ObservationRepository;

public class RecordObservationCommand implements Command {

    private final ObservationRepository observationRepository;
    private final Observation observation;
    private final String payloadJson;

    private Observation savedObservation;

    public RecordObservationCommand(ObservationRepository observationRepository,
                                    Observation observation,
                                    String payloadJson) {
        this.observationRepository = observationRepository;
        this.observation = observation;
        this.payloadJson = payloadJson;
    }

    @Override
    public void execute() {
        savedObservation = observationRepository.save(observation);
    }

    @Override
    public String getCommandType() { return "RECORD_OBSERVATION"; }

    @Override
    public String getPayload() { return payloadJson; }

    public Observation getSavedObservation() { return savedObservation; }
}

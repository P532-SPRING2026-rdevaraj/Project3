package com.tracker.engine.command;

import com.tracker.domain.Observation;
import com.tracker.domain.ObservationStatus;
import com.tracker.resourceaccess.ObservationRepository;

/**
 * Command pattern — wraps the "reject observation" operation (F8).
 * Week 1 class, unchanged for Week 2.
 * getPayload() already embeds observationId so AuditableCommandLog can
 * parse the affected observation ID from the stored JSON without any new
 * interface or method on this class.
 */
public class RejectObservationCommand implements Command {

    private final ObservationRepository observationRepository;
    private final Observation observation;
    private final String reason;

    public RejectObservationCommand(ObservationRepository observationRepository,
                                    Observation observation,
                                    String reason) {
        this.observationRepository = observationRepository;
        this.observation = observation;
        this.reason = reason;
    }

    @Override
    public void execute() {
        observation.setStatus(ObservationStatus.REJECTED);
        observation.setRejectionReason(reason);
        observationRepository.save(observation);
    }

    @Override
    public String getCommandType() { return "REJECT_OBSERVATION"; }

    @Override
    public String getPayload() {
        return "{\"observationId\":" + observation.getId()
            + ",\"reason\":\"" + escapeJson(reason == null ? "" : reason) + "\"}";
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

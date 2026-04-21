package com.tracker.dto;

import com.tracker.domain.*;
import java.time.Instant;


/**
 * Unified response DTO for any Observation (Measurement or CategoryObservation).
 * The controller maps domain objects here — never exposes JPA entities directly.
 */
public class ObservationResponse {

    private Long id;
    private String type;           // "MEASUREMENT" or "CATEGORY"
    private Long patientId;
    private Instant recordingTime;
    private Instant applicabilityTime;
    private ObservationStatus status;
    private String rejectionReason;
    private ObservationSource source;   // MANUAL or INFERRED (Change 4)
    private boolean anomalyFlag;        // true when outside normalMin/normalMax (Change 2)

    // Protocol (optional)
    private Long protocolId;
    private String protocolName;

    // Measurement fields
    private Long phenomenonTypeId;
    private String phenomenonTypeName;
    private Double amount;
    private String unit;

    // CategoryObservation fields
    private Long phenomenonId;
    private String phenomenonName;
    private Presence presence;

    public static ObservationResponse from(Observation obs) {
        ObservationResponse r = new ObservationResponse();
        r.id = obs.getId();
        r.patientId = obs.getPatient().getId();
        r.recordingTime = obs.getRecordingTime();
        r.applicabilityTime = obs.getApplicabilityTime();
        r.status = obs.getStatus();
        r.rejectionReason = obs.getRejectionReason();
        r.source = obs.getSource();
        r.anomalyFlag = obs.isAnomalyFlag();
        if (obs.getProtocol() != null) {
            r.protocolId = obs.getProtocol().getId();
            r.protocolName = obs.getProtocol().getName();
        }
        if (obs instanceof Measurement m) {
            r.type = "MEASUREMENT";
            r.phenomenonTypeId = m.getPhenomenonType().getId();
            r.phenomenonTypeName = m.getPhenomenonType().getName();
            r.amount = m.getAmount();
            r.unit = m.getUnit();
        } else if (obs instanceof CategoryObservation c) {
            r.type = "CATEGORY";
            r.phenomenonId = c.getPhenomenon().getId();
            r.phenomenonName = c.getPhenomenon().getName();
            r.phenomenonTypeId = c.getPhenomenon().getPhenomenonType().getId();
            r.phenomenonTypeName = c.getPhenomenon().getPhenomenonType().getName();
            r.presence = c.getPresence();
        }
        return r;
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public Long getPatientId() { return patientId; }
    public Instant getRecordingTime() { return recordingTime; }
    public Instant getApplicabilityTime() { return applicabilityTime; }
    public ObservationStatus getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public Long getProtocolId() { return protocolId; }
    public String getProtocolName() { return protocolName; }
    public Long getPhenomenonTypeId() { return phenomenonTypeId; }
    public String getPhenomenonTypeName() { return phenomenonTypeName; }
    public Double getAmount() { return amount; }
    public String getUnit() { return unit; }
    public Long getPhenomenonId() { return phenomenonId; }
    public String getPhenomenonName() { return phenomenonName; }
    public Presence getPresence() { return presence; }
    public ObservationSource getSource() { return source; }
    public boolean isAnomalyFlag() { return anomalyFlag; }
}

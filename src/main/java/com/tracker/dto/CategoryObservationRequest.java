package com.tracker.dto;

import com.tracker.domain.Presence;
import java.time.Instant;

public class CategoryObservationRequest {
    private Long patientId;
    private Long phenomenonId;
    private Presence presence;
    private Long protocolId;
    private Instant applicabilityTime;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getPhenomenonId() { return phenomenonId; }
    public void setPhenomenonId(Long phenomenonId) { this.phenomenonId = phenomenonId; }

    public Presence getPresence() { return presence; }
    public void setPresence(Presence presence) { this.presence = presence; }

    public Long getProtocolId() { return protocolId; }
    public void setProtocolId(Long protocolId) { this.protocolId = protocolId; }

    public Instant getApplicabilityTime() { return applicabilityTime; }
    public void setApplicabilityTime(Instant applicabilityTime) { this.applicabilityTime = applicabilityTime; }
}

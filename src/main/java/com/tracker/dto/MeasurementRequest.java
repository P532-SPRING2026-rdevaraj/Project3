package com.tracker.dto;

import java.time.Instant;

public class MeasurementRequest {
    private Long patientId;
    private Long phenomenonTypeId;
    private Double amount;
    private String unit;
    private Long protocolId;
    private Instant applicabilityTime;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getPhenomenonTypeId() { return phenomenonTypeId; }
    public void setPhenomenonTypeId(Long phenomenonTypeId) { this.phenomenonTypeId = phenomenonTypeId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Long getProtocolId() { return protocolId; }
    public void setProtocolId(Long protocolId) { this.protocolId = protocolId; }

    public Instant getApplicabilityTime() { return applicabilityTime; }
    public void setApplicabilityTime(Instant applicabilityTime) { this.applicabilityTime = applicabilityTime; }
}

package com.tracker.dto;

/** Request body for POST /api/observations/{id}/reject (F8). */
public class RejectObservationRequest {
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

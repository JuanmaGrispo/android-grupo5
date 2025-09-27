package com.example.tp0gym.ui.model;

import androidx.annotation.Nullable;

public class ReservationUiModel {

    private final String reservationId;  // id de la reserva
    private final String sessionId;      // id de la sesión
    private final String title;          // p.ej. nombre de la clase
    private final String subtitle;       // p.ej. "Disciplina · Sede"
    private final String formattedDate;  // "01 Oct 2025 - 10:00"
    private final String status;         // "Confirmada", "Cancelada", etc.

    public ReservationUiModel(String reservationId,
                              String sessionId,
                              String title,
                              String subtitle,
                              String formattedDate,
                              String status) {
        this.reservationId = reservationId;
        this.sessionId = sessionId;
        this.title = title;
        this.subtitle = subtitle;
        this.formattedDate = formattedDate;
        this.status = status;
    }

    public String getReservationId() { return reservationId; }
    public String getSessionId() { return sessionId; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getFormattedDate() { return formattedDate; }
    public String getStatus() { return status; }

    // Helper inmutable para actualizar estado en el adapter
    public ReservationUiModel withStatus(String newStatus) {
        return new ReservationUiModel(
                reservationId,
                sessionId,
                title,
                subtitle,
                formattedDate,
                newStatus
        );
    }

    // (Opcional) equals/hashCode si querés DiffUtil
    @Override public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ReservationUiModel)) return false;
        ReservationUiModel other = (ReservationUiModel) obj;
        return reservationId != null && reservationId.equals(other.reservationId);
    }

    @Override public int hashCode() {
        return reservationId == null ? 0 : reservationId.hashCode();
    }
}
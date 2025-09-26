package com.example.tp0gym.repository.dto;

public class ReservationDto {
    public String id;

    // Estado de la reserva en sí
    public String status;      // CONFIRMED / CANCELED
    public String canceledAt;
    public String createdAt;
    public String updatedAt;

    // Relación con la sesión
    public SessionDto session;

    public static class SessionDto {
        public String id;
        public String startAt;       // 2025-09-19T19:59:00.000Z
        public Integer durationMin;  // 60
        public Integer capacity;     // 20
        public Integer reservedCount;// 1
        public String status;        // SCHEDULED
        public String createdAt;
        public String updatedAt;
    }

    // Helper rápido
    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(status);
    }
}
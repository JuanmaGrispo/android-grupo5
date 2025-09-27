// app/src/main/java/com/example/tp0gym/repository/dto/ReservationDto.java
package com.example.tp0gym.repository.dto;

public class ReservationDto {
    public String id;

    // Estado de la reserva
    public String status;      // CONFIRMED / CANCELED
    public String canceledAt;
    public String createdAt;
    public String updatedAt;

    // Relación: sesión
    public SessionDto session;

    // (Si alguna vez necesitás mostrar datos del usuario, agregalo acá)
    // public UserDto user;

    public static class SessionDto {
        public String id;
        public String startAt;        // 2025-09-19T19:59:00.000Z
        public Integer durationMin;
        public Integer capacity;
        public Integer reservedCount;
        public String status;         // SCHEDULED / ...

        public ClassRefDto classRef;  // 👈 ahora viene de backend
        public BranchDto branch;      // 👈 idem

        public String createdAt;
        public String updatedAt;
    }

    public static class ClassRefDto {
        public String id;
        public String title;              // p.ej. "Funcional 2"
        public String description;
        public String discipline;         // p.ej. "Funcional"
        public Integer defaultDurationMin;
        public Integer defaultCapacity;
        public String instructorName;     // puede venir null
        public String locationName;       // puede venir null
        public String locationAddress;    // puede venir null
        public String createdAt;
        public String updatedAt;
    }

    public static class BranchDto {
        public String id;
        public String name;       // p.ej. "Fit palermo"
        public String location;   // p.ej. "Palermo"
        public String createdAt;
        public String updatedAt;
    }

    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(status);
    }
}
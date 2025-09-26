// modelo/AttendanceDto.java
package com.example.tp0gym.modelo;

import com.google.gson.annotations.SerializedName;

public class AttendanceDto {
    @SerializedName("id")        private String id;
    @SerializedName("createdAt") private String createdAt;
    @SerializedName("session")   private SessionDto session;

    public String getId()        { return id; }
    public String getCreatedAt() { return createdAt; }
    public SessionDto getSession(){ return session; }

    public static class SessionDto {
        @SerializedName("id")         private String id;
        @SerializedName("startAt")    private String startAt;       // "2025-09-26T06:40:00.000Z"
        @SerializedName("durationMin")private Integer durationMin;
        @SerializedName("status")     private String status;
        @SerializedName("classRef")   private ClassRefDto classRef;

        public String getId()        { return id; }
        public String getStartAt()   { return startAt; }
        public Integer getDurationMin(){ return durationMin; }
        public String getStatus()    { return status; }
        public ClassRefDto getClassRef(){ return classRef; }
    }

    public static class ClassRefDto {
        @SerializedName("id")                private String id;
        @SerializedName("title")             private String title;          // <<--- viene como "title"
        @SerializedName("discipline")        private String discipline;     // ej: "Funcional"
        @SerializedName("instructorName")    private String instructorName; // puede ser null
        @SerializedName("locationName")      private String locationName;   // opcional

        public String getId()             { return id; }
        public String getTitle()          { return title; }
        public String getDiscipline()     { return discipline; }
        public String getInstructorName() { return instructorName; }
        public String getLocationName()   { return locationName; }
    }
}

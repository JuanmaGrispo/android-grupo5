package com.example.tp0gym.modelo;

import com.google.gson.annotations.SerializedName;

public class AttendanceDto {
    @SerializedName("id")
    private String id;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("user")
    private UserDto user;
    
    @SerializedName("session")
    private SessionDto session;

    // Constructors
    public AttendanceDto() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public SessionDto getSession() {
        return session;
    }

    public void setSession(SessionDto session) {
        this.session = session;
    }

    // Nested SessionDto class
    public static class SessionDto {
        @SerializedName("id")
        private String id;
        
        @SerializedName("startAt")
        private String startAt;
        
        @SerializedName("durationMin")
        private int durationMin;
        
        @SerializedName("capacity")
        private int capacity;
        
        @SerializedName("reservedCount")
        private int reservedCount;
        
        @SerializedName("status")
        private String status;
        
        @SerializedName("createdAt")
        private String createdAt;
        
        @SerializedName("updatedAt")
        private String updatedAt;
        
        @SerializedName("classRef")
        private ClassRefDto classRef;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStartAt() {
            return startAt;
        }

        public void setStartAt(String startAt) {
            this.startAt = startAt;
        }

        public int getDurationMin() {
            return durationMin;
        }

        public void setDurationMin(int durationMin) {
            this.durationMin = durationMin;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getReservedCount() {
            return reservedCount;
        }

        public void setReservedCount(int reservedCount) {
            this.reservedCount = reservedCount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public ClassRefDto getClassRef() {
            return classRef;
        }

        public void setClassRef(ClassRefDto classRef) {
            this.classRef = classRef;
        }
    }

    // Nested ClassRefDto class
    public static class ClassRefDto {
        @SerializedName("id")
        private String id;
        
        @SerializedName("title")
        private String title;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("discipline")
        private String discipline;
        
        @SerializedName("defaultDurationMin")
        private int defaultDurationMin;
        
        @SerializedName("defaultCapacity")
        private int defaultCapacity;
        
        @SerializedName("instructorName")
        private String instructorName;
        
        @SerializedName("locationName")
        private String locationName;
        
        @SerializedName("locationAddress")
        private String locationAddress;
        
        @SerializedName("createdAt")
        private String createdAt;
        
        @SerializedName("updatedAt")
        private String updatedAt;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDiscipline() {
            return discipline;
        }

        public void setDiscipline(String discipline) {
            this.discipline = discipline;
        }

        public int getDefaultDurationMin() {
            return defaultDurationMin;
        }

        public void setDefaultDurationMin(int defaultDurationMin) {
            this.defaultDurationMin = defaultDurationMin;
        }

        public int getDefaultCapacity() {
            return defaultCapacity;
        }

        public void setDefaultCapacity(int defaultCapacity) {
            this.defaultCapacity = defaultCapacity;
        }

        public String getInstructorName() {
            return instructorName;
        }

        public void setInstructorName(String instructorName) {
            this.instructorName = instructorName;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public String getLocationAddress() {
            return locationAddress;
        }

        public void setLocationAddress(String locationAddress) {
            this.locationAddress = locationAddress;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}

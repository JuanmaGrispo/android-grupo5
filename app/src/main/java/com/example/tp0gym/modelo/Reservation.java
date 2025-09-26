package com.example.tp0gym.modelo;

import java.time.LocalDateTime;
import java.util.Date;

public class Reservation {
    private final String id;
    private final String title;
    private final String discipline;
    private final Date dateTime;
    private final String location;
    private final boolean confirmed;

    public Reservation(String id, String title, String discipline, Date dateTime,
                       String location, boolean confirmed) {
        this.id = id;
        this.title = title;
        this.discipline = discipline;
        this.dateTime = dateTime;
        this.location = location;
        this.confirmed = confirmed;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDiscipline() { return discipline; }
    public Date getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public boolean isConfirmed() { return confirmed; }
}
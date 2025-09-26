package com.example.tp0gym.ui.model;

public class ReservationUiModel {
    public final String title;
    public final String subtitle;       // "Yoga · Sede A"
    public final String formattedDate;  // "01 Oct 2025 - 10:00"
    public final String status;

    public ReservationUiModel(String title, String subtitle, String formattedDate, String status) {
        this.title = title;
        this.subtitle = subtitle;
        this.formattedDate = formattedDate;
        this.status = status;
    }


    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getStatus() {
        return status;
    }


}
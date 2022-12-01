package com.example.mine.model;

import android.net.Uri;

import java.time.LocalDate;

public class CalendarData {
    public LocalDate localDate;
    public Uri imageUri;

    public CalendarData(LocalDate day) {
        this.localDate = day;
    }
}

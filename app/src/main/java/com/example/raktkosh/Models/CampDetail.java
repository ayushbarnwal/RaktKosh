package com.example.raktkosh.Models;

import java.util.Stack;

public class CampDetail {

    String camp_date, camp_month, camp_year;
    String camp_start_time;
    String camp_end_time;
    String camp_address;
    String latitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    String longitude;

    public String getSet_reminder() {
        return set_reminder;
    }

    public void setSet_reminder(String set_reminder) {
        this.set_reminder = set_reminder;
    }

    String set_reminder;

    public String getReminder_time() {
        return reminder_time;
    }

    public void setReminder_time(String reminder_time) {
        this.reminder_time = reminder_time;
    }

    String reminder_time;

    public CampDetail(String camp_id) {
        this.camp_id = camp_id;
    }

    public String getCamp_id() {
        return camp_id;
    }

    public void setCamp_id(String camp_id) {
        this.camp_id = camp_id;
    }

    String camp_id;

    public CampDetail() {}

    public CampDetail(String camp_date, String camp_start_time, String camp_end_time, String camp_address) {
        this.camp_date = camp_date;
        this.camp_start_time = camp_start_time;
        this.camp_end_time = camp_end_time;
        this.camp_address = camp_address;
    }

    public CampDetail(String camp_date, String camp_month, String camp_year, String camp_start_time, String camp_end_time, String camp_address, String reminder_time, String set_reminder, String latitude, String longitude) {
        this.camp_date = camp_date;
        this.camp_month = camp_month;
        this.camp_year = camp_year;
        this.camp_start_time = camp_start_time;
        this.camp_end_time = camp_end_time;
        this.camp_address = camp_address;
        this.reminder_time = reminder_time;
        this.set_reminder = set_reminder;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCamp_date() {
        return camp_date;
    }

    public void setCamp_date(String camp_date) {
        this.camp_date = camp_date;
    }

    public String getCamp_start_time() {
        return camp_start_time;
    }

    public void setCamp_start_time(String camp_start_time) {
        this.camp_start_time = camp_start_time;
    }

    public String getCamp_end_time() {
        return camp_end_time;
    }

    public void setCamp_end_time(String camp_end_time) {
        this.camp_end_time = camp_end_time;
    }

    public String getCamp_address() {
        return camp_address;
    }

    public void setCamp_address(String camp_address) {
        this.camp_address = camp_address;
    }

    public String getCamp_month() {
        return camp_month;
    }

    public void setCamp_month(String camp_month) {
        this.camp_month = camp_month;
    }

    public String getCamp_year() {
        return camp_year;
    }

    public void setCamp_year(String camp_year) {
        this.camp_year = camp_year;
    }
}

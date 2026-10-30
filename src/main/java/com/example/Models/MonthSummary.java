package com.example.Models;

public class MonthSummary {
    private int fullDays;
    private int shortDays;
    private int leaveDays;
    private int totalMin;

    public MonthSummary() { }                       // Spring Data needs this to read from MongoDB

    public MonthSummary(int fullDays, int shortDays, int leaveDays, int totalMin) {
        this.fullDays = fullDays;
        this.shortDays = shortDays;
        this.leaveDays = leaveDays;
        this.totalMin = totalMin;
    }

    //These methods are used for setting summary, in the onClickLogout method in 
    //AttendanceMonthService
    public int getFullDays() { return fullDays; }
    public void setFullDays(int fullDays) { this.fullDays = fullDays; }
    public int getShortDays() { return shortDays; }
    public void setShortDays(int shortDays) { this.shortDays = shortDays; }
    public int getLeaveDays() { return leaveDays; }
    public void setLeaveDays(int leaveDays) { this.leaveDays = leaveDays; }
    public int getTotalMin() { return totalMin; }
    public void setTotalMin(int totalMin) { this.totalMin = totalMin; }
}

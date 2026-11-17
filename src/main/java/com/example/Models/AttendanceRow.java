package com.example.Models;

import java.time.Instant;
import java.time.LocalDate;

public class AttendanceRow {
        private LocalDate date;
        private DayType status;
        private Instant inTime;
        private Instant outTime;
        private Integer totalDurationMin;
        private String leaveType;

  public AttendanceRow() {};
  public AttendanceRow(LocalDate date, DayType status, Instant inTime, Instant outTime, Integer totalDurationMin, String leaveType) {
        this.date = date;
        this.status = status;
        this.inTime = inTime;
        this.outTime = outTime;
        this.totalDurationMin = totalDurationMin;
        this.leaveType = leaveType;
    }
public static AttendanceRow fromEntry(LocalDate date, DayEntry e) {
    return new AttendanceRow(date, e.getType(), e.getInTime(), e.getOutTime(),
                             e.getTotalDurationMin(), e.getLeaveType());
}

    public static AttendanceRow notLoggedInToday(LocalDate date, DayType status) {
        return new AttendanceRow(date, status, null, null, null, null);
    }

    //-------------------Getters and Setters--------------------//
    public LocalDate getDate() { return date;}
    public void setDate(LocalDate date) { this.date = date; }
    public DayType getStatus() { return status; }           
    public void setStatus(DayType status) { this.status = status; }
    public Instant getInTime() { return inTime; }
    public void setInTime(Instant inTime) { this.inTime = inTime; }
    public Instant getOutTime() { return outTime; }
    public void setOutTime(Instant outTime) { this.outTime = outTime; }
    public Integer getTotalDurationMin() { return totalDurationMin; }
    public void setTotalDurationMin(Integer totalDurationMin) { this.totalDurationMin = totalDurationMin; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }  

}

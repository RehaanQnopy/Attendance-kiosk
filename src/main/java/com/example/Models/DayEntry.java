package com.example.Models;

import java.time.Instant;

import org.bson.types.ObjectId;

public class DayEntry {
    private DayType type;
    private Instant inTime;
    private Instant outTime;
    private Integer totalDurationMin;
    private ObjectId leaveId;
    private String leaveType;

    public DayEntry() { }

    public static DayEntry workStartedAt(Instant inTime) {
        DayEntry entry = new DayEntry();
        entry.setType(DayType.WORK);
        entry.setInTime(inTime);
        return entry;
    }

    public DayType getType() { return type; }
    public void setType(DayType type) { this.type = type; }
    public Instant getInTime() { return inTime; }
    public void setInTime(Instant inTime) { this.inTime = inTime; }
    public Instant getOutTime() { return outTime; }
    public void setOutTime(Instant outTime) { this.outTime = outTime; }
    public Integer getTotalDurationMin() { return totalDurationMin; }
    public void setTotalDurationMin(Integer totalDurationMin) { this.totalDurationMin = totalDurationMin; }
    public ObjectId getLeaveId() { return leaveId; }
    public void setLeaveId(ObjectId leaveId) { this.leaveId = leaveId; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
}

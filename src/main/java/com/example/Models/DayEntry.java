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

    //This method provides the 
    public static DayEntry workStartedAt(Instant inTime) {
        DayEntry entry = new DayEntry();

        entry.setType(DayType.WORK);
        entry.setInTime(inTime);
        return entry;
    }

    public DayType getType() { return type; }
    //The below method is used at clockIn logic, to set the type of work {WORK, LEAVE}
    public void setType(DayType type) { this.type = type; }
    public Instant getInTime() { return inTime; }
    //The below method is used to set the inTime in the login clock logic
    public void setInTime(Instant inTime) { this.inTime = inTime; }
    //The below methods, are used for adding the out_time in the onClickLogout() method logic
    public Instant getOutTime() { return outTime; }
    public void setOutTime(Instant outTime) { this.outTime = outTime; }
    //The below methods are also used in the onClickLogout() method logic in the AttendanceMonthService file
    public Integer getTotalDurationMin() { return totalDurationMin; }
    public void setTotalDurationMin(Integer totalDurationMin) { this.totalDurationMin = totalDurationMin; }

    public ObjectId getLeaveId() { return leaveId; }
    public void setLeaveId(ObjectId leaveId) { this.leaveId = leaveId; }
    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }
}

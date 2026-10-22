package com.example.attendanceMarker.model;

import java.time.Instant;

// import org.bson.types.ObjectId;

public class DayEntry {
    private DayType type;
    private Instant inTime;
    private Instant outTime;
    private Integer totalDurationMin;
    // private ObjectId leaveId;
    // private String leaveType;

    public DayEntry() {} //An empty bean is required 

    //Below method is static, meaning can be accesses directly via class name, also
    //this provides the new DayEntry Object, with time and type already filled
    public static DayEntry workStartedAt(Instant in_time)
    {
        DayEntry entry= new DayEntry();
        entry.inTime= in_time;
        //We can add as many DayType's and directly access them anywhere
        entry.setType(DayType.WORK);
        return entry;
    }

    //All these methods are for fetching the info about the particular user's per day data
    public DayType getType() { return type; }
    //setType is used in workStartedAt method, to set the value either as WORK or LEAVE
    public void setType(DayType type) { this.type = type; }

    //Return the getInTime 
    // public Instant getInTime() { return inTime; }
    // public void setInTime(Instant inTime) { this.inTime = inTime; }

    //The below method allows us to add the out_time for the user
    public Instant getOutTime() { return outTime; }
    public void setOutTime(Instant outTime) { this.outTime = outTime; }

    //This gives the total_duration available only after the user has logged out
    public Integer getTotalDurationMin() { return totalDurationMin; }
    public void setTotalDurationMin(Integer totalDurationMin) { this.totalDurationMin = totalDurationMin;}  
}

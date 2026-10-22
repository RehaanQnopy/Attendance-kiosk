package com.example.attendanceMarker.model;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "attendance_months")
@CompoundIndex(def = "{'userId': 1, 'month': 1}", unique = true)
public class AttendanceMonth {

    @Id
    //This one stores the id in the string format of the AttendanceMonth object 
    private String id;
    //This stores the user to which this particualr AttendanceMonth object belongs to 
    /*
        eg: users:{ _id: 6710a1, name: "A" }
            attendance_months: { _id: 7720ff, userId: 6710a1, month: "2026-10", days: {...} }
    */
    private ObjectId userId;                    // reference to users
    private String month;                       // "2026-10"
    private Map<String, DayEntry> days;         // embedded, key = day number "19"
    private MonthSummary summary;               // embedded

    public AttendanceMonth() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ObjectId getUserId() { return userId; }
    public void setUserId(ObjectId userId) { this.userId = userId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public Map<String, DayEntry> getDays() { return days; }
    public void setDays(Map<String, DayEntry> days) { this.days = days; }
    public MonthSummary getSummary() { return summary; }
    public void setSummary(MonthSummary summary) { this.summary = summary; }
}


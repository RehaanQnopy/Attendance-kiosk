package com.example.Models;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "attendance_months")
//This is for implementing Indexing and for fast lookup
@CompoundIndex (def = "{'userId': 1, 'month': 1}", unique = true)
public class AttendanceMonth {

    @Id 
    private String id;
    private String userId;                    // reference to users
    private String month;                       // "2026-10"
    private Map<String, DayEntry> days;         // embedded, key = day number "19"
    private MonthSummary summary;               // embedded

    public AttendanceMonth() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    //The below method is being used in the AttendanceMonthService file to get the whole
    //days map.
    public Map<String, DayEntry> getDays() { return days; }
    //This method is beign used in the AttendanceMonthService file to Store the today's day and DayEntry obejct 
    public void setDays(Map<String, DayEntry> days) { this.days = days; }
    public MonthSummary getSummary() { return summary; }
    public void setSummary(MonthSummary summary) { this.summary = summary; }
}

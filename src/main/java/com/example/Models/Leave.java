package com.example.Models;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "leaves")
//The indexing for the status works in the lexicographically manner, 
// Like APPLIED is sorted up of PENDING which is sorted up of REJECTED
@CompoundIndex(def= "{'managerId': 1, 'status': 1, 'appliedAt': -1 }")
@CompoundIndex(def="{'employeeId':1, 'appliedAt': -1}")
public class Leave {
    @Id 
    private String id; //@Id, handles the conversion for the ObjectId we get from mongoDb to String and vice-versa.

    private ObjectId employeeId;
    //Why the need of keeping the managerId, if we are storing the approval id in StautsHistory
    //Cause first the indexing is there
    //and second cause, for the manager dashboard all the users data should get visible, so the indexing sorts by the maanger
    //and the status{PENDING}.
    private ObjectId managerId;

    private String employeeName;
    private String managerName;


    private LocalDate fromDate;
    private LocalDate toDate;
    private int daysCount;

    private String type;
    private String reason;
    private String status;
    
    private Instant appliedAt;
    /* The below stores the Histroy of status being changed, wpplied by who and approved by who*/
    private List<StatusHistory> statusHistory;


    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ObjectId getEmployeeId() { return employeeId; }
    public void setEmployeeId(ObjectId userId) { this.employeeId = userId; }

    public ObjectId getManagerId() { return managerId; }
    public void setManagerId(ObjectId managerId) { this.managerId = managerId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public int getDaysCount() { return daysCount; }
    public void setDaysCount(int daysCount) { this.daysCount = daysCount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Instant getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Instant appliedAt) { this.appliedAt = appliedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<StatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<StatusHistory> statusHistory) { this.statusHistory = statusHistory; }
}





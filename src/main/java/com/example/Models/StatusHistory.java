package com.example.Models;

import java.time.Instant;

import org.bson.types.ObjectId;

/*Could have used Lombok,as it automatically sets the setter and getter, realized later */
public class StatusHistory {
    private String status; // Stauts of the leave ACCEPTED | REJECTED
    private ObjectId by; //who approved it 
    private Instant at; // when did he applied
    private String comment;  // Any comment by the approver or rejecter 

    public StatusHistory() {}; //Required an empty Class constructor for 

    //Required for DataSeed file for initilizing the data at starting 
    public StatusHistory(String status, Instant at, ObjectId by, String comment) {
        this.status = status;
        this.at = at;
        this.by = by;
        this.comment = comment;
    }

    //Also, one Bug was, the getters and setter must be set as publci, so that they become accesible for outer world
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getWhenApprived() { return at; }
    public void setWhenApproved(Instant at) { this.at = at; }

    public ObjectId getWhoApproved() { return by; }
    public void setWhoApproved(ObjectId by) { this.by = by; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}







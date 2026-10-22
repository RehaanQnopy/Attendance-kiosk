package com.example.attendanceMarker.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "users")
public class User {

    @Id 
    //Replaced the ObjectId with the string
    private String id;
    private String name;
    /*
 * WHY THIS IS INDEXED:
 * - Performance: Converts slow collection scans (COLLSCAN) into fast index lookups (IXSCAN) for login queries.
 * - Data Integrity: Enforces a strict database constraint preventing duplicate accounts, even during race conditions.
 */
    @Indexed(unique = true)
    private String email;   
    private String role;                        // EMPLOYEE | MANAGER
    private ObjectId managerId;                 // reference to another user

    public User() { }

    //Also all these getters and setters method are declared cause the variables are private 
    //Alse, the methods declared here are used in the Controller to get the values from the Response we get from the dataBase

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public ObjectId getManagerId() { return managerId; }
    public void setManagerId(ObjectId managerId) { this.managerId = managerId; }
   
}

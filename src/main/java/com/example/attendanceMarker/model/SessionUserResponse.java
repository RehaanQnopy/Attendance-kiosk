package com.example.attendanceMarker.model;

public class SessionUserResponse {

    private String id;
    private String name;
    private String role; 
    private String email;

    SessionUserResponse(String id, String name, String role, String email)
    {
        this.id=id;
        this.name=name;
        this.role=role;
        this.email=email;
    };


    // public String getId() {return this.id;}
    // public String getName() {return this.name;}
    // public String getRole() {return this.role;}
    // public String getEmail() {return this.email;}
}

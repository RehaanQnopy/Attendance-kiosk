package com.example.Models;

import java.time.Instant;

public class ManagerUsersView {
    private String userId;
    private String name;
    private Instant in_time;
    private Instant out_time;
    private String email;
    private String status;

    ManagerUsersView() { }

    //If you won't declare the constructor as public, then it will be package-private and not accessible from other packages.
    //Meaning, you will get the error 
    // "The constructor ManagerUsersView(String, String, Instant, Instant, String, String) is not visible" 
    // when you try to create an instance of ManagerUsersView from a different package.
    public ManagerUsersView(String userId, String name, Instant in_time, Instant out_time, String email, String status) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.in_time = in_time;
        this.out_time = out_time;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }   
    public String getName() {
        return name;
    }
    public Instant getIn_time() {
        return in_time;     
    }
    public Instant getOut_time() {
        return out_time;
    }
    public String getStatus() {
        return status;  
    }
    public String getEmail() {
        return email;
    }
}

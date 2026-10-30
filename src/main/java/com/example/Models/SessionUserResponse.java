package com.example.Models;
import java.util.List;


public class SessionUserResponse {
    private final String id;
    private final String name;
    private final List<String> role;

    public SessionUserResponse(String id, String name, List<String> role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getRole() { return role; }
}
package com.example.Models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    //Stored as ObjectId in MongoDB, handled as String in Java!
    //Springboot automatically converts the ObjectID received from mongoDb into the String 
    private String id; 
    private String name;
    @Indexed (unique = true)
    private String email;                       // stored in lowercase
    private String role;                        // EMPLOYEE | MANAGER
    private ObjectId managerId;                 // reference to another user

    public User() { }

    //The below methods are used for filling the details fro the foundUser in the AuthController file
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    //The below methods are used for filling the details fro the foundUser in the AuthController file
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    //The below methods are used for filling the details fro the foundUser in the AuthController file
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    //The below methods are used for filling the details fro the foundUser in the AuthController file
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public ObjectId getManagerId() { return managerId; }
    public void setManagerId(ObjectId managerId) { this.managerId = managerId; }
}

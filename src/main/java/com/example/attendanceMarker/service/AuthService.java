package com.example.attendanceMarker.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.attendanceMarker.model.User;
import com.example.attendanceMarker.repository.UserRepository;


@Service 
public class AuthService
{

    private UserRepository userRepo;

   
     // This method ensures that if the user exist its data is returned otherwise 
    public Optional<User> isUserThere(String email)
    {
        if(email==null)return Optional.empty();
        //this ensures that the email stored in the backend for finding the user matches what user fills
        String trimmedEmail = email==null? "": email.trim().toLowerCase();
        //this is where the Query call is being made to the backend
        return this.userRepo.findByEmail(trimmedEmail);
    }

/* 
    When your application boots up, Spring Data scans your repository interface and breaks your method name down into structural pieces using a regex parser:
1. find: Spring recognizes this as a read operation. It translates this to MongoDB's db.collection.find(...) command.
2. By: This acts as a semantic delimiter. It tells Spring, "The query criteria rules are starting right after this word."
3. Email: Spring looks at your User model properties. It finds a field named private String email;. It matches the word perfectly (ignoring the case of the first letter) and converts it to the query key: { "email": ... }.
4. (String email): Spring takes the argument you pass into the method at runtime and plugs it into the query value slot.

*/

};
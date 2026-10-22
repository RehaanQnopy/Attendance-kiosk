package com.example.attendanceMarker.controller;


import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.attendanceMarker.model.SessionUserResponse;
import com.example.attendanceMarker.model.User;
import com.example.attendanceMarker.service.AuthService;

//this provides the access to the session
import jakarta.servlet.http.HttpSession;

@RestController 
@RequestMapping("api/auth")
public class AuthController {

    public AuthService authService;
    public HttpSession session;


    //The below method checks if the user exist based on the email it provides, and then send back id, name, role, and email 
    //in Response
    @PostMapping("/sign-in")
    public ResponseEntity<SessionUserResponse> verify(@RequestBody String email)
    {
        Optional<User> curr_user= this.authService.isUserThere(email);
        if(curr_user.isPresent())
        {
            //The curr_user is wrapped inside the Optional
            //the below .get() unwraps the value 
            User foundUser = curr_user.get();
            //the below adds the userID to the session of the browser
            this.session.setAttribute("userdId", foundUser.getId());
            SessionUserResponse response = new SessionUserResponse(foundUser.getId(), foundUser.getName(), foundUser.getRole(), foundUser.getEmail());
            return ResponseEntity.ok(response);
        }   
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
    
}

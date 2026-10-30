package com.example.Controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Models.SessionUserResponse;
import com.example.Models.User;
import com.example.Services.AuthService;

import jakarta.servlet.http.HttpSession;

@RestController 
@RequestMapping ("/api/auth")
public class AuthController {

    @Autowired 
    private AuthService authService;
    private HttpSession session;
    
    @PostMapping("/user")
    public ResponseEntity<SessionUserResponse> isValidUser(@RequestBody String email)
    {
        Optional<User> foundUser = this.authService.isUserExisting(email);
        if(foundUser.isPresent())
        {
            //since the response we get is wrapped inside the Optional block
            //therefore we use the .get() to unwrap it and get the values out of it
            User curr_user = foundUser.get();
            SessionUserResponse response = new SessionUserResponse(curr_user.getId().toString(), curr_user.getName(), curr_user.getRole());
            //Here we are making the session also for the verified user
            session.setAttribute("user_id", curr_user.getId());
            return ResponseEntity.status(HttpStatus.FOUND).body(response);
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}

package com.example.Controllers;

import java.util.Map;
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
@RequestMapping("auth")
public class AuthController {

    @Autowired 
    private AuthService authService;

    //Fix 1: HttpSession is NOT a class field anymore (that was always null, causing the NullPointerException).
    //Spring gives us the real session when we ask for it as a method parameter.
    //Fix 2: Angular sends { "email": "..." }, so we read it as a Map and pick the "email" key out of it.
    @PostMapping("/user")
    public ResponseEntity<SessionUserResponse> isValidUser(@RequestBody Map<String, String> body, HttpSession session)
    {
        String email = body.get("email");
        Optional<User> foundUser = this.authService.isUserExisting(email);
        if(foundUser.isPresent())
        {
            //since the response we get is wrapped inside the Optional block
            //therefore we use the .get() to unwrap it and get the values out of it
            User curr_user = foundUser.get();
            SessionUserResponse response = new SessionUserResponse(curr_user.getId().toString(), curr_user.getName(), curr_user.getRole());
            //Here we are making the session also for the verified user
            session.setAttribute("userId", curr_user.getId());
            //Fix 3: 200 OK for a successful login (FOUND is a 302 redirect status)
            return ResponseEntity.ok(response);
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
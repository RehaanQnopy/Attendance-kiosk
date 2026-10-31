package com.example.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Models.User;
import com.example.Repositories.UserRepository;

@Service 
public class AuthService {

   @Autowired 
   private UserRepository userRepo;

   
   public Optional<User> isUserExisting(String email)
   {
        if(email==null) return Optional.empty();
        //Below cleanign the email, ensures the consistency between what user fills 
        //and what is filled in the backend
        String cleanedEmail= email.trim().toLowerCase();
        return this.userRepo.findByEmail(cleanedEmail);    
   }
    
}

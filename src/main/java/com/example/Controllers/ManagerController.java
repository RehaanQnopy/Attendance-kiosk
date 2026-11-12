package com.example.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Models.ManagerUsersView;
import com.example.Models.User;
import com.example.Repositories.UserRepository;
import com.example.Services.ManagerService;

@RestController 
@RequestMapping("manager")
public class ManagerController {

    @Autowired 
    private UserRepository userRepo;
    @Autowired 
    private ManagerService managerService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers()
    {
        List<User> users = this.userRepo.findAll();
        if(users.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        else return ResponseEntity.status(HttpStatus.OK).body(users);
    }
    @GetMapping ("home")
    public ResponseEntity<List<ManagerUsersView>> getTodaysAttendance(){

        Optional<List<ManagerUsersView>> usersAttendance = Optional.empty();
        List<User> allUsers = this.userRepo.findAll();
        if(allUsers.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        usersAttendance = this.managerService.getUsersAttendanceForManager(allUsers); 

        if(usersAttendance.isPresent())
        {
            return ResponseEntity.status(HttpStatus.OK).body(usersAttendance.get());
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            
    }
    
}

package com.example.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Models.Leave;

public interface LeaveRepository extends MongoRepository<Leave, String>{

    //For the user dashboard's Leave section
    Optional<List<Leave>> findByEmployeeIdOrderByAppliedAtDesc(String employeeId);
     // Manager's inbox, for example every PENDING request for this manager.
    // Served by the index {managerId: 1, status: 1, appliedAt: -1}
    Optional<List<Leave>> findByManagerIdAndStatusOrderByAppliedAtDesc(String managerId, String status);
  
}

package com.example.attendanceMarker.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.attendanceMarker.model.AttendanceMonth;

@Repository 
//Repositories are interfaces not classes 
//The string here is the data type of the Primary key declared in the AttendanceMonth module
public interface AttedanceMonthRepository extends MongoRepository<AttendanceMonth, String > {
    Optional<AttendanceMonth> findByUserIdAndEmail(String userId, String email);
}

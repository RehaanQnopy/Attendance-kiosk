package com.example.Repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Models.AttendanceMonth;

public interface AttendanceMonthRepository extends MongoRepository<AttendanceMonth, String> {
    //This is the customized method which will be executed as Query in the mongoDB,
    //as find based on userId and month values and we pass the values as userId and month.
    Optional<AttendanceMonth> findByUserIdAndMonth(String userId, String month);
}

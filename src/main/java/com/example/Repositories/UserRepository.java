package com.example.Repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Models.User;

public interface UserRepository extends MongoRepository<User, String> {
	//This is the manual method we have created to search the user via email, 
	//It converts back into the query to find by column email, this is used in the 
	//AuthService file.
    Optional<User> findByEmail(String email);
}

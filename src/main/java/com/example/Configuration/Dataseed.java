package com.example.Configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.example.Models.User;


@Profile ("Dev")
//Configuration tells the Spring-boot that the below class is a Config file
@Configuration 
public class Dataseed {

    /*
        We are defining a CommandLineRunner. 
        Spring Boot is programmed to look for any @Bean that returns a CommandLineRunner 
        and automatically execute its code (args -> { ... }) immediately after the application starts up.

        Also this file runs every time app runs, so make a check that, we don't have any prior data
    */

    @Bean 
    //Also the Bean is declared over the CommandLineRunner method, Bean can be declared over methods itseld, so that
    //Spring-boot engine registers the method as a Bean and stored the OBject it receives 
    CommandLineRunner seed(MongoTemplate mongo) 
    {
        return new CommandLineRunner()
        {
            @Override 
            /*
                Very important learnings:
                    1. the String... means, that we currently don't know how many inputs of type string or string[] are going to 
                        be passed here, in this method, so we declare String...
                    2. Why neede here
                        eg: When you launch a Spring Boot application from your terminal, you can pass command-line configuration 
                        arguments like this:

                        java -jar myapp.jar --server.port=9090 --spring.profiles.active=dev

                        Spring catches those trailing items (--server.port=9090 and --spring.profiles.active=dev) and drops them 
                        straight into that String... args array so your database seeder or startup logic can read them if necessary.
            */
            public void run(String... args) throws Exception
            {
                if(mongo.count(org.springframework.data.mongodb.core.query.Query(), User.class))
                {

                }


            } 
        }
    }


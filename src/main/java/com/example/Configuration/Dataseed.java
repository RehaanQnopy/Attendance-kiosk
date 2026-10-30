package com.example.Configuration;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.example.Models.AttendanceMonth;
import com.example.Models.DayEntry;
import com.example.Models.DayType;
import com.example.Models.Leave;
import com.example.Models.MonthSummary;
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
                //User.class points to the User class in your model file, it tells the mongoDb, which Document to search in
                //Scans the whole file 
                if(mongo.count(new org.springframework.data.mongodb.core.query.Query(), User.class) > 0)
                {
                    
                    System.out.println("DataSeed: Server already contains the data");
                    return ;         // already seeded, do nothing
                }
                User manager = createUser(mongo, "Onkar Kulkarni", "onkar.kulkarni@qnopy.com", List.of("MANAGER", "EMPLOYEE"), null);
                // Converts the manager's ID (Hex String) into a binary BSON ObjectId object for MongoDB
                ObjectId managerId = new ObjectId(manager.getId());

                User Rehaan = createUser(mongo, "Rehaan Makhija",   "rehaan.makhija@qnopy.com",   List.of("EMPLOYEE"), managerId);
                User Harshad = createUser(mongo, "Harshad Bhandare", "harshad.bhandare@qnopy.com", List.of("EMPLOYEE"), managerId);
                User Saurabh = createUser(mongo, "Saurabh Gogte",    "saurabh@qnopy.com",    List.of("EMPLOYEE"), managerId);

                List<Leave> RehaanLeaves = new ArrayList<>();
                RehaanLeaves.add(createLeave(mongo, Rehaan, manager, "SICK",   LocalDate.of(2026, 9, 10),  LocalDate.of(2026, 9, 11),  "APPROVED", "Fever"));
                RehaanLeaves.add(createLeave(mongo, Rehaan, manager, "CASUAL", LocalDate.of(2026, 10, 14), LocalDate.of(2026, 10, 15), "APPROVED", "Family function"));
                RehaanLeaves.add(createLeave(mongo, Rehaan, manager, "ANNUAL", LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 13), "PENDING",  "Short trip"));
 
                List<Leave> HarshadLeaves = new ArrayList<>();
                HarshadLeaves.add(createLeave(mongo, Harshad, manager, "CASUAL", LocalDate.of(2026, 9, 17),  LocalDate.of(2026, 9, 17),  "APPROVED", "Personal work"));
                HarshadLeaves.add(createLeave(mongo, Harshad, manager, "SICK",   LocalDate.of(2026, 10, 21), LocalDate.of(2026, 10, 22), "APPROVED", "Dental surgery"));
                HarshadLeaves.add(createLeave(mongo, Harshad, manager, "CASUAL", LocalDate.of(2026, 11, 19), LocalDate.of(2026, 11, 20), "PENDING",  "Wedding"));
 
                List<Leave> SaurabhLeaves = new ArrayList<>();
                SaurabhLeaves.add(createLeave(mongo, Saurabh, manager, "ANNUAL", LocalDate.of(2026, 9, 24),  LocalDate.of(2026, 9, 25),  "APPROVED", "Vacation"));
                SaurabhLeaves.add(createLeave(mongo, Saurabh, manager, "CASUAL", LocalDate.of(2026, 10, 8),  LocalDate.of(2026, 10, 8),  "APPROVED", "Bank work"));
                SaurabhLeaves.add(createLeave(mongo, Saurabh, manager, "CASUAL", LocalDate.of(2026, 10, 28), LocalDate.of(2026, 10, 29), "REJECTED", "Project deadline week"));
                SaurabhLeaves.add(createLeave(mongo, Saurabh, manager, "SICK",   LocalDate.of(2026, 11, 5),  LocalDate.of(2026, 11, 5),  "PENDING",  "Migraine"));
            

                saveMonths(mongo, manager, new ArrayList<Leave>(), 0);
                saveMonths(mongo, Rehaan, RehaanLeaves, 1);
                saveMonths(mongo, Harshad, HarshadLeaves, 2);
                saveMonths(mongo, Saurabh, SaurabhLeaves, 3);
 
                System.out.println("DevDataSeeder: seeded 4 users, 10 leaves, 8 attendance months.");
            }
        };
    };

    //----------------------Helper Functions------------------------
    //The below method helps in building the user 
    private User createUser(MongoTemplate mongo, String name, String email, List<String>role, ObjectId managerID)
    {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setRole(role);
        u.setManagerId(managerID);

        return mongo.insert(u);
    }

    private Leave createLeave(MongoTemplate mongo, User employee, User manager, String type,
                              LocalDate from, LocalDate to, String status, String reason) {
        Leave leave = new Leave();
        leave.setEmployeeId(new ObjectId((employee.getId())));
        leave.setEmployeeName(employee.getName());
        leave.setManagerId(new ObjectId(manager.getId()));
        leave.setManagerName(manager.getName());
        leave.setType(type);
        leave.setReason(reason);
        leave.setFromDate(from);
        leave.setToDate(to);
        leave.setDaysCount((int) ChronoUnit.DAYS.between(from, to) + 1);
        leave.setStatus(status);
        leave.setAppliedAt(from.minusDays(5).atStartOfDay().toInstant(ZoneOffset.UTC));
        leave.setStatusHistory(new ArrayList<>());     // left empty in the seed
        return mongo.insert(leave);
    }

    private AttendanceMonth buildMonth(String userId, int year, int month, int lastDay,
                                       int shift, List<Leave> leaves) {
        Map<String, DayEntry> days = new HashMap<>();
        MonthSummary summary = new MonthSummary(0, 0, 0, 0);
 
        for (int d = 1; d <= lastDay; d++) {
            LocalDate date = LocalDate.of(year, month, d);
 
            // no entry on weekends
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
 
            // a day covered by an approved leave becomes a LEAVE entry linked to that leave
            Leave approvedLeave = findApprovedLeaveOn(leaves, date);
            if (approvedLeave != null) {
                DayEntry leaveDay = new DayEntry();
                leaveDay.setType(DayType.LEAVE);
                leaveDay.setLeaveId(new ObjectId(approvedLeave.getId()));
                leaveDay.setLeaveType((approvedLeave.getType()));
                days.put(String.valueOf(d), leaveDay);
                summary.setLeaveDays(summary.getLeaveDays() + 1);
                continue;
            }
 
            // otherwise a normal work day: some short, some full
            int minutes = (d + shift) % 4 == 0 ? 300 : 480 + (d % 3) * 20;
            Instant in = LocalDateTime.of(year, month, d, 4, 0).toInstant(ZoneOffset.UTC);   // 09:30 IST
 
            DayEntry workDay = DayEntry.workStartedAt(in);
            workDay.setOutTime(in.plusSeconds(minutes * 60L));
            workDay.setTotalDurationMin(minutes);
            days.put(String.valueOf(d), workDay);
 
            if (minutes >= 480) {
                summary.setFullDays(summary.getFullDays() + 1);
            } else {
                summary.setShortDays(summary.getShortDays() + 1);
            }
            summary.setTotalMin(summary.getTotalMin() + minutes);
        }
 
        AttendanceMonth attendanceMonth = new AttendanceMonth();
        attendanceMonth.setUserId(userId);
        attendanceMonth.setMonth(String.format("%d-%02d", year, month));
        attendanceMonth.setDays(days);
        attendanceMonth.setSummary(summary);
        return attendanceMonth;
    }

     
    private void saveMonths(MongoTemplate mongo, User user, List<Leave> leaves, int shift) {
        mongo.insert(buildMonth(user.getId(), 2026, 9, 30, shift, leaves));
        mongo.insert(buildMonth(user.getId(), 2026, 10, 27, shift, leaves));
    }

    private Leave findApprovedLeaveOn(List<Leave> leaves, LocalDate date) {
        for (Leave leave : leaves) {
            boolean approved = "APPROVED".equals(leave.getStatus());
            boolean covers = !date.isBefore(leave.getFromDate()) && !date.isAfter(leave.getToDate());
            if (approved && covers) {
                return leave;
            }
        }
        return null;
    }
};

// @RestController
// @RequestMapping("/api/attendance")
// public class AttendanceController {

//     private final AttendanceService attendanceService;

//     public AttendanceController(AttendanceService attendanceService) {
//         this.attendanceService = attendanceService;
//     }

//     @GetMapping("/months/{monthKey}")
//     public AttendanceMonthResponse getMonth(@SessionAttribute("userId") String userId,
//                                             @PathVariable String monthKey) {
//         return attendanceService.getMonth(new ObjectId(userId), monthKey);
//     }

//     @PostMapping("/clock-in")
//     public AttendanceMonthResponse clockIn(@SessionAttribute("userId") String userId) {
//         return attendanceService.clockIn(new ObjectId(userId));
//     }

//     @PostMapping("/clock-out")
//     public AttendanceMonthResponse clockOut(@SessionAttribute("userId") String userId) {
//         return attendanceService.clockOut(new ObjectId(userId));
//     }
// }

// @RestController
// @RequestMapping("/api/attendance")
// public class AttendanceController {

//     private final AttendanceService attendanceService;

//     public AttendanceController(AttendanceService attendanceService) {
//         this.attendanceService = attendanceService;
//     }

//     @GetMapping("/months/{monthKey}")
//     public AttendanceMonthResponse getMonth(@SessionAttribute("userId") String userId,
//                                             @PathVariable String monthKey) {
//         return attendanceService.getMonth(new ObjectId(userId), monthKey);
//     }

//     @PostMapping("/clock-in")
//     public AttendanceMonthResponse clockIn(@SessionAttribute("userId") String userId) {
//         return attendanceService.clockIn(new ObjectId(userId));
//     }

//     @PostMapping("/clock-out")
//     public AttendanceMonthResponse clockOut(@SessionAttribute("userId") String userId) {
//         return attendanceService.clockOut(new ObjectId(userId));
//     }
// }
// use attendance_db
// const managerId = db.users.insertOne({
//   name: "Test Manager", email: "manager@test.com", role: "MANAGER", managerId: null
// }).insertedId
// db.users.insertMany([
//   { name: "Test Employee A", email: "a@test.com", role: "EMPLOYEE", managerId: managerId },
//   { name: "Test Employee B", email: "b@test.com", role: "EMPLOYEE", managerId: managerId }
// ])

// package com.example.attendanceMarker.model;

// import java.time.Instant;
// import java.time.YearMonth;
// import java.time.ZoneId;
// import java.util.HashMap;

// import javax.xml.datatype.Duration;

// import org.bson.types.ObjectId;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.index.Indexed;
// import org.springframework.data.mongodb.core.mapping.Document;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.SessionAttribute;
// import org.springframework.web.server.ResponseStatusException;

// @Document (collection = "users")
// public class User {

//     @Id 
//     //Replaced the ObjectId with the string
//     private String id;
//     private String name;
//     /*
//  * WHY THIS IS INDEXED:
//  * - Performance: Converts slow collection scans (COLLSCAN) into fast index lookups (IXSCAN) for login queries.
//  * - Data Integrity: Enforces a strict database constraint preventing duplicate accounts, even during race conditions.
//  */
//     @Indexed(unique = true)
//     private String email;   
//     private String role;                        // EMPLOYEE | MANAGER
//     private ObjectId managerId;                 // reference to another user

//     public User() { }

//     //Also all these getters and setters method are declared cause the variables are private 

//     public String getId() { return id; }
//     public void setId(String id) { this.id = id; }
//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }
//     public String getEmail() { return email; }
//     public void setEmail(String email) { this.email = email; }
//     public String getRole() { return role; }
//     public void setRole(String role) { this.role = role; }
//     public ObjectId getManagerId() { return managerId; }
//     public void setManagerId(ObjectId managerId) { this.managerId = managerId; }
// }
// @Service
// public class AttendanceService {

//     private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
//     private final AttendanceMonthRepository repo;

//     public AttendanceService(AttendanceMonthRepository repo) { this.repo = repo; }

//     public AttendanceMonth getMonth(String userId, String monthKey) {
//         return repo.findByUserIdAndMonth(userId, monthKey)
//                    .orElseGet(() -> newMonth(userId, monthKey));    // returned, not saved
//     }

//     public AttendanceMonth clockIn(String userId) {
//         Instant now = Instant.now();
//         String monthKey = toMonthKey(now), dayKey = toDayKey(now);

//         AttendanceMonth month = repo.findByUserIdAndMonth(userId, monthKey)
//                                     .orElseGet(() -> newMonth(userId, monthKey));
//         if (month.getDays().containsKey(dayKey))
//             throw new ResponseStatusException(HttpStatus.CONFLICT, "Already clocked in today");

//         month.getDays().put(dayKey, DayEntry.workStartedAt(now));   // the in-time goes here
//         return repo.save(month);
//     }

//     public AttendanceMonth clockOut(String userId) {
//         Instant now = Instant.now();
//         String monthKey = toMonthKey(now), dayKey = toDayKey(now);

//         AttendanceMonth month = repo.findByUserIdAndMonth(userId, monthKey)
//                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Not clocked in today"));
//         DayEntry today = month.getDays().get(dayKey);
//         if (today == null || today.getType() != DayType.WORK)
//             throw new ResponseStatusException(HttpStatus.CONFLICT, "Not clocked in today");
//         if (today.getOutTime() != null)
//             throw new ResponseStatusException(HttpStatus.CONFLICT, "Already clocked out today");

//         int minutes = (int) Duration.between(today.getInTime(), now).toMinutes();
//         today.setOutTime(now);
//         today.setTotalDurationMin(minutes);

//         MonthSummary summary = month.getSummary();
//         if (minutes >= 480) summary.setFullDays(summary.getFullDays() + 1);
//         else summary.setShortDays(summary.getShortDays() + 1);
//         summary.setTotalMin(summary.getTotalMin() + minutes);

//         return repo.save(month);
//     }

//     private AttendanceMonth newMonth(String userId, String monthKey) {
//         AttendanceMonth month = new AttendanceMonth();
//         month.setUserId(userId);
//         month.setMonth(monthKey);
//         month.setDays(new HashMap<>());
//         month.setSummary(new MonthSummary());
//         return month;
//     }

//     private String toMonthKey(Instant i) { return YearMonth.from(i.atZone(IST)).toString(); }
//     private String toDayKey(Instant i)   { return String.valueOf(i.atZone(IST).getDayOfMonth()); }
// }
package com.example.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.Models.AttendanceMonth;
import com.example.Services.AttendanceMonthService;

@RestController 
@RequestMapping ("attendance")
public class AttendanceMarkerController {

    @Autowired 
    private AttendanceMonthService attendanceMonthService;

    @PostMapping("login")
    /* 
        The @SessionAttribute annotation is a Spring Boot directive used inside your 
        RestController methods to automatically fetch data that was previously stored in the user's 
        HTTP Session.
    */
    public ResponseEntity<AttendanceMonth> clockIn(@SessionAttribute("userId" ) String userId)
    {
        return attendanceMonthService.onClickLogin(userId)
        .map((Response) -> ResponseEntity.status(HttpStatus.OK).body(Response))
        .orElseGet(()-> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PostMapping("logout")
    public ResponseEntity<AttendanceMonth> clockOut(@SessionAttribute("userId" ) String userId)
    {
        return attendanceMonthService.onCLickLogout(userId)
        .map((Response) -> ResponseEntity.status(HttpStatus.OK).body(Response))
        .orElseGet(()-> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping ("/month/{monthKey}")
    public ResponseEntity<AttendanceMonth> calendarPreview(@SessionAttribute("userId") String userId, 
                @PathVariable String monthKey)
    {
        return attendanceMonthService.getMonth(userId, monthKey)
        .map((Response)-> ResponseEntity.status(HttpStatus.OK).body(Response))
        .orElseGet(()-> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}

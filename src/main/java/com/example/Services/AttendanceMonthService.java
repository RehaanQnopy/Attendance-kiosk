package com.example.Services;

import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.Models.AttendanceMonth;
import com.example.Models.DayEntry;
import com.example.Models.DayType;
import com.example.Models.MonthSummary;
import com.example.Repositories.AttendanceMonthRepository;

public class AttendanceMonthService {

    //Sets the time Zone to indian time zone
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    
    @Autowired 
    private AttendanceMonthRepository attendanceRepo;

    public Optional<AttendanceMonth> onClickLogin(String userId)
    {
        Instant now = Instant.now();

        //YearMonth is a built-in java class that extracts Year and Month from the time stamp
        String monthKey=YearMonth.from(now.atZone(IST)).toString(); //"2026-10"
        //.getDayOfMonth gives the current day date and String converts it into "22"
        String dayKey=String.valueOf(now.atZone(IST).getDayOfMonth());

        AttendanceMonth ExistingMonth;

        Optional<AttendanceMonth> month = this.attendanceRepo.findByUserIdAndMonth(userId, monthKey);
        if(month.isPresent())
        {
            //we unwrap the existing month object, so that we could use the methods of the 
            //AttendanceMonth object
            ExistingMonth=month.get();
        }
        //If no entry for the current month exist then, we create AttendanceMonth object 
        //for the current month
        else 
        {
            ExistingMonth = new AttendanceMonth();
            ExistingMonth.setUserId(userId);
            ExistingMonth.setMonth(monthKey);
            ExistingMonth.setDays(new HashMap<>());
            ExistingMonth.setSummary(new MonthSummary());
           
        }
                    //The below check ensures that the same user is not getting logged in twice in a day
            if(ExistingMonth.getDays().containsKey(dayKey))
            {
                return Optional.empty();
            }
            ExistingMonth.getDays().put(dayKey,DayEntry.workStartedAt(now));   
            // return attendanceRepo.save(ExistingMonth); <- This line gave error for the type conversion 
            //Optional<AttendanceMonth> and AttendanceMonth cause the method is of type Optional

            //This ensures that the save happens for the Database and response is sent back as 
            //Optional<AttendanceMonth>
            return Optional.of(attendanceRepo.save(ExistingMonth));
            
    }
//--------------------------------------------------------------------------------------------------------------------------------------------//
    public Optional<AttendanceMonth> onCLickLogout(String userId)
    {
        Instant now = Instant.now();
        //YearMonth is a built-in java class that extracts Year and Month from the time stamp
        String monthKey=YearMonth.from(now.atZone(IST)).toString(); //"2026-10"
        //.getDayOfMonth gives the current day date and String converts it into "22"
        String dayKey=String.valueOf(now.atZone(IST).getDayOfMonth());

        AttendanceMonth ExistingMonth;
        Optional<AttendanceMonth> isLoginThereForThisDayAndMonth = attendanceRepo.findByUserIdAndMonth(userId, monthKey);
        if(isLoginThereForThisDayAndMonth.isEmpty())
        {
            //This check ensures that, if no login happened for this particular day, then an
            //empty Optional is sent back
            return Optional.empty();
        }
        else 
        {
            //unwraps the AtttendanceMonth object from the Optional cover
            ExistingMonth = isLoginThereForThisDayAndMonth.get();
            //Here we extract the DayEntry object of today as onCLickLogin() would have created the 
            //todays object already
            DayEntry today = ExistingMonth.getDays().get(dayKey);
            if(today==null || today.getType()!=DayType.WORK)
            {
                return Optional.empty();
            }
            //Here the check ensures that, if user has already marked its logout or the user has not logged in 
            //itself
            else if(today.getOutTime()!=null || today.getInTime()==null)
            {
                return Optional.empty();
            }
                //We calculate the total duration of the user
                int minute = (int)Duration.between(today.getInTime(), now).toMinutes();
                today.setOutTime(now);
                today.setTotalDurationMin(minute);

            //Here we are updating the summart element of the AttendanceMonth object, which is already
            //existing
            MonthSummary summary= ExistingMonth.getSummary();
            //The methods that were declared in the MonthSummary model are being handy here
            //as these methods help us get the calues stored in the Object that were created
            if(minute>=480)summary.setFullDays(summary.getFullDays()+1);
            else
            {
                summary.setShortDays(summary.getShortDays()+1);
            }
            summary.setTotalMin(summary.getTotalMin()+minute);
            return Optional.of(attendanceRepo.save(ExistingMonth));
        }
    }

//--------------------------------------------------------------------------------------------------------------------------------------------//

//This method is for returning the particular AttendanceMonth data for a Particular userId   
public Optional<AttendanceMonth> getMonth(String userId, String monthKey)
    {
        Optional<AttendanceMonth> reqMonth = this.attendanceRepo.findByUserIdAndMonth(userId,monthKey);
        if(reqMonth.isPresent())
        {
            AttendanceMonth resMonth= reqMonth.get();
            return Optional.of(resMonth);
        }
        else
        {
          AttendanceMonth month = new AttendanceMonth();
            month.setUserId(userId);
            month.setMonth(monthKey);
            month.setDays(new HashMap<>());
            month.setSummary(new MonthSummary());
            return Optional.of(month);
        }
    }
    
}

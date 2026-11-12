package com.example.Services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Models.AttendanceMonth;
import com.example.Models.DayEntry;
import com.example.Models.DayType;
import com.example.Models.ManagerUsersView;
import com.example.Models.MonthSummary;
import com.example.Models.User;
import com.example.Repositories.AttendanceMonthRepository;

@Service 
public class ManagerService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    Instant now = Instant.now();
    private static final int FULL_DAY_MIN = 480;

    @Autowired 
    private AttendanceMonthRepository attendanceRepo;
    public Optional<AttendanceMonth> editDay(String userId, String monthKey, int day, Instant in, Instant out, String editedBy)
    {
        YearMonth ym;
        try {
            ym = YearMonth.parse(monthKey);
        } catch (Exception e) {
            return Optional.empty();
        }
        LocalDate date = ym.atDay(day);
        if(date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Cannot edit future dates");
        if(in ==null || out==null) throw new IllegalArgumentException("You missed either the in_time or out-time");
        if(in.isAfter(out)) throw new IllegalArgumentException("In time cannot be after Out time");

        Optional<AttendanceMonth> month = this.attendanceRepo.findByUserIdAndMonth(userId, monthKey);
        if(!month.isPresent())
        {
            return Optional.empty();
        }
        AttendanceMonth existingMonth = month.get();
        MonthSummary summary = existingMonth.getSummary();
        DayEntry entry = existingMonth.getDays().get(String.valueOf(day));
            if (in == null && out == null) {
                 entry.setType(DayType.LEAVE);
             } else {
                entry.setType(DayType.WORK);
             }
        entry.setInTime(in);
        entry.setOutTime(out);
        entry.setTotalDurationMin(out == null ? null : (int) Duration.between(in, out).toMinutes());
        recomputeMonthSummary(existingMonth);
        return Optional.of(this.attendanceRepo.save(existingMonth));

    }

//---------------------------------------------------------------------------------------------------------------------------------------------------//
    public void recomputeMonthSummary(AttendanceMonth month)
    {
        MonthSummary summary= month.getSummary();
        int shortDays=0, leaveDays=0;
        for(DayEntry e: month.getDays().values())
        {
            if(e.getType()==DayType.WORK && e.getTotalDurationMin()!=null 
                && e.getTotalDurationMin()<FULL_DAY_MIN) shortDays++;
            else if(e.getInTime()==null && e.getOutTime()==null) leaveDays++;
            else shortDays++;
        }
        summary.setShortDays(shortDays);
        summary.setLeaveDays(leaveDays);
        month.setSummary(summary);

    }
//---------------------------------------------------------------------------------------------------------------------------------------------------//
    public Optional<List<ManagerUsersView>> getUsersAttendanceForManager(List<User> users)
{
    // FIX 3: compute "now" inside the method. If "now" is a field on the service,
    // it is set once at startup, so the date never moves forward and "today" goes stale.
    Instant now = Instant.now();
    String monthKey = YearMonth.from(now.atZone(IST)).toString();

    // FIX 2: the "days" map in MongoDB is keyed by the day of the month ("5", "12"),
    // not by the month ("2026-11"). Build the key the same way the clock-in code saves it.
    String dayKey = String.valueOf(now.atZone(IST).getDayOfMonth());

    List<ManagerUsersView> response = new ArrayList<>();
    for(User user: users)
    {
        Optional<AttendanceMonth> month = this.attendanceRepo.findByUserIdAndMonth(user.getId(), monthKey);
        if(month.isPresent())
        {
            //Here we are just un-wraaping the AttendanceMonth data from the Optional cover and storing it in a variable
            AttendanceMonth monthData = month.get();

            // ISSUE: this was .get(monthKey), which looks up "2026-11" in a map keyed "5", "12".
            // It always returned null, so everyone fell into the "Absent" branch.
            DayEntry today = monthData.getDays().get(dayKey);
            if(today!=null)
            {
                Instant isInTimeNull = today.getInTime();
                Instant isOutTimeNull = today.getOutTime();
                String status;
                if(isInTimeNull == null && isOutTimeNull == null) status = "Absent";
                else if(isInTimeNull != null && isOutTimeNull == null) status = "Working";
                else if(isInTimeNull != null && isOutTimeNull != null) status = "Left";
                else status = "Unknown";

                ManagerUsersView managerViewForUsers = new ManagerUsersView(
                        user.getId(),
                        user.getName(),
                        today.getInTime(),
                        today.getOutTime(),
                        user.getEmail(),
                        status );
                //Very important to note here, here we are adding the Object's reference to the list 
                //and when this list is returned to the controller, the controller will convert this list into JSON 
                // and send it to the front-end
                response.add(managerViewForUsers);
            }
            else 
            {
                // No entry for today in this month's record -> Absent
                ManagerUsersView managerViewForUsers = new ManagerUsersView(
                        user.getId(),
                        user.getName(),
                        null,
                        null,
                        user.getEmail(),
                        "Absent" );

                response.add(managerViewForUsers);
            }
        }   
        else 
        {
            // FIX 1: a user with no record this month is Absent. Before, this was
            // "return Optional.empty()", which threw away the whole list for everyone.
            response.add(new ManagerUsersView(
                    user.getId(),
                    user.getName(),
                    null,
                    null,
                    user.getEmail(),
                    "Absent" ));
        }
    }
    return Optional.of(response);
}                       
    
}

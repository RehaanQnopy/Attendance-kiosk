package com.example.Services;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.Models.AttendanceMonth;
import com.example.Models.AttendanceRow;
import com.example.Models.DayEntry;
import com.example.Models.DayType;
import com.example.Models.MonthSummary;
import com.example.Repositories.AttendanceMonthRepository;
import com.mongodb.DuplicateKeyException;

@Service 
public class AttendanceMonthService {

    /* 
        If you declared private MongoTemplate mongoTemplate; without @Autowired or without including it in a constructor,
        Java leaves it as null
    */
    @Autowired 
    private final MongoTemplate mongoTemplate=null;

    //Sets the time Zone to indian time zone
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    //Here We declare the weekends as Saturday and Sunday, so that we could use it 
    // in the typeFor() method to set the type of the day
    // private static final Set<DayOfWeek> WEEKENDS = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY); 
    
    @Autowired 
    private AttendanceMonthRepository attendanceRepo;

    public Optional<AttendanceMonth> onClickLogin(String userId)
    {
        Instant nowInstant = Instant.now();
        Long now = Instant.now().toEpochMilli(); //13-digit timestamp stored in DB

        //YearMonth is a built-in java class that extracts Year and Month from the time stamp
        String monthKey=YearMonth.from(nowInstant.atZone(IST)).toString(); //"2026-10"
        //.getDayOfMonth gives the current day date and String converts it into "22"
        String dayKey=String.valueOf(nowInstant.atZone(IST).getDayOfMonth());

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
        Instant nowInstant = Instant.now();
        Long now = Instant.now().toEpochMilli(); // 13-digit timestamp fro database
        //YearMonth is a built-in java class that extracts Year and Month from the time stamp
        String monthKey=YearMonth.from(nowInstant.atZone(IST)).toString(); //"2026-10"
        //.getDayOfMonth gives the current day date and String converts it into "22"
        String dayKey=String.valueOf(nowInstant.atZone(IST).getDayOfMonth());

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
                int minute = (int)((now-today.getInTime())/60000);
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
public Optional<AttendanceMonth> getMonth(String userId, String monthKey)
{
    Optional<AttendanceMonth> month = this.attendanceRepo.findByUserIdAndMonth(userId, monthKey);
    if(month.isEmpty())
    {
        return Optional.empty();
    }
    AttendanceMonth existingMonth = month.get();
    return Optional.of(existingMonth);
} 
//This method is for returning the particular AttendanceMonth data for a Particular userId   
public List<AttendanceRow> particularMonthView(String userId, String monthKey)
    {
        YearMonth ym = YearMonth.parse(monthKey);
        LocalDate today = LocalDate.now(IST);

        AttendanceMonth doc = getOrSetMonth(userId, monthKey);

        //Very importat that the <> diamond operators are for type inference, 
        //They can be left empty as they take shape of the type form the variable you are storing it 
        // so that we don't have to write the type again and again
        Map<String, DayEntry> days = doc.getDays()==null? new HashMap<String, DayEntry>() : new HashMap<>(doc.getDays());

        //Learned new things:
        //Criteria acts as where clause , so that we could get the target document from the DB, it will be passed into the new Query()
        Criteria criteria = Criteria.where("userId").is(userId).and("month").is(monthKey);
        
        //Update is for setting the new values for the document that we are fetching from the DB
        Update update = new Update();
        //The below variable is for checking if any changes have been made to the document, 
        // so that we could save it back to the DB
        boolean dirty =false;

        for(int day =1; day<=ym.lengthOfMonth(); day++)
        {
            //.atDay() usage here is:
            // ym is a java.time.YearMonth (such as 2026-11) and day is an integer (such as 16), .atDay(16) combines 
            // them into a full calendar date (2026-11-16).
            LocalDate date = ym.atDay(day);  //2026-11-16

            //String.valueOf(), because there are no methods for primitive(int) value and .valueOf
            String dayKey = String.valueOf(day); //16

            //The below conditions are ensuring that, if there is dayEntry for this particular 
            // day or the day is not before today's date then continue
            if(days.containsKey(dayKey) || !date.isBefore(today)) continue;

            //We create a new DayEntry object for the days where user has no entry, and set the type of the day 
            // as per the typeFor() method
            DayEntry emptyEntry = DayEntry.noUserEntry(typeFor(date, today));
            days.put(dayKey, emptyEntry);
            // We store the new DayEntry object in the update object, so that we could save it back to the DB
            update.set("days."+dayKey, emptyEntry); //days.16 = emptyEntry
            //Dirty is set to true even fro next iteration cause here the backend call is not being fro every loop only
            //the update object is being set, so that we could save it back to the DB after the loop ends
            dirty =true;
        }
        if(dirty)
                {
                    mongoTemplate.updateFirst(new Query(criteria), update, AttendanceMonth.class);
                }
        //Building the AttendanceRow, response for the frontend, so that it could be displayed in the UI
        List<AttendanceRow> rows = new ArrayList<AttendanceRow>();
        for(int d=1; d<=ym.lengthOfMonth(); d++)
            {
                LocalDate date = ym.atDay(d);
                DayEntry entry = days.get(String.valueOf(d));
                 if (entry != null) {                                  // stored entry wins (incl. future leaves)
                rows.add(AttendanceRow.fromEntry(date, entry));
                } else if (date.equals(today)) {                      // today, nothing stored yet
                 rows.add(AttendanceRow.notLoggedInToday(date, typeFor(date, today)));
              }  
                else 
                {
                    rows.add(AttendanceRow.notLoggedInToday(date, typeFor(date, today)));
                }
            }
            return rows;                                               
    }



    //---------------HELPER FUNCTIONS---------------------------//
    private AttendanceMonth getOrSetMonth(String userId, String monthKey)
    {
        Optional<AttendanceMonth> exist = attendanceRepo.findByUserIdAndMonth(userId, monthKey);
        if(exist.isPresent())
        {
            return exist.get();
        }
    
        AttendanceMonth m = new AttendanceMonth();
        m.setUserId(userId);        // same as onClickLogin; leave the id to MongoDB
        m.setMonth(monthKey);
                m.setDays(new HashMap<>());
                m.setSummary(new MonthSummary());
            //Here we ensure that after the new block is created then we return back the new object created
            try {
                //if block is new like no prior block is there then we return the new one
                return attendanceRepo.save(m);
            }
            //In catch block we catch the duplicate error like if accidenntally there exist more than one of the same object then 
            catch (DuplicateKeyException e)
            {
                return attendanceRepo.findByUserIdAndMonth(userId, monthKey).orElseThrow();

            }
            
     }
     private DayType typeFor(LocalDate d, LocalDate today)
     {
        DayOfWeek w = d.getDayOfWeek();
        if(w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY) return DayType.WEEKEND;
        if(d.isBefore(today))return DayType.ABSENT;
        if(d.isAfter(today))return DayType.NOT_CLOCKED_IN; // default to not clocked in for future days
        return DayType.NOT_CLOCKED_IN; // default to absent for past days without entries
     }
}
    


package apps.nerdyginger.pocketpantry.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.room.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.models.UserSchedule;

// Helpful methods for dates and date ranges related to scheduling
// Last edited: 2/18/2020
public class ScheduleHelper {
    private Context context;
    private Calendar cal;
    SimpleDateFormat format;


    public ScheduleHelper(Context context) {
        this.context = context;
        cal = Calendar.getInstance();
        setWeekStartDay();
        format = new SimpleDateFormat("MM/dd/yyyy", Locale.US); //Does this need to become a user pref?
    }

    private void setWeekStartDay() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String day = prefs.getString("firstDayOfWeek", "Sunday");
        switch(Objects.requireNonNull(day)) {
            case "Monday":
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                break;
            case "Tuesday":
                cal.setFirstDayOfWeek(Calendar.TUESDAY);
                break;
            case "Wednesday":
                cal.setFirstDayOfWeek(Calendar.WEDNESDAY);
                break;
            case "Thursday":
                cal.setFirstDayOfWeek(Calendar.THURSDAY);
                break;
            case "Friday":
                cal.setFirstDayOfWeek(Calendar.FRIDAY);
                break;
            case "Saturday":
                cal.setFirstDayOfWeek(Calendar.SATURDAY);
                break;
            default:
                cal.setFirstDayOfWeek(Calendar.SUNDAY);
                break;
        }
    }

    public String getCurrentDate() {
        cal = Calendar.getInstance();
        return format.format(cal.getTime());
    }

    // Sets cal to a specific date
    private void setDate(String date) {
        cal = Calendar.getInstance();
        String[] dateParts = date.split("/");
        cal.set(Integer.parseInt(dateParts[2].replaceFirst("^0+(?!$)", "")),
                Integer.parseInt(dateParts[0].replaceFirst("^0+(?!$)", "")) - 1, // 0 is Jan, so adjust accordingly
                Integer.parseInt(dateParts[1].replaceFirst("^0+(?!$)", "")));
    }

    public long convertDateToLong(String date) {
        long newDate = 0;
        try {
            Date dateObj = format.parse(date);
            newDate =  dateObj.getTime();
        } catch(Exception e) {
            Log.e("-PRO TIPS! AND ERRORS-", "Error converting date to long: " + e.toString());
        }
        return newDate;
    }

    // Returns the start date for the last stored date in the helper
    private String getWeekStartDate() {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset;
        if (dayOfWeek == 1) {
            offset = -6;
        } else {
            offset = 2 - dayOfWeek;
        }
        cal.add(Calendar.DAY_OF_YEAR, offset);
        return format.format(cal.getTime());
    }

    public String getWeekStartDate(String date) {
        setDate(date);
        return getWeekStartDate();
    }

    public String getCurrentWeekStartDate() {
        cal = Calendar.getInstance();
        return getWeekStartDate();
    }

    // Returns the end date for the last stored date in the helper
    // NOTE: always use getWeekStartDate() first!
    private String getWeekEndDate() {
        cal.add(Calendar.DAY_OF_YEAR, 6);
        return format.format(cal.getTime());
    }

    // Public method to get the week end date for a given date
    public String getWeekEndDate(String date) {
        setDate(date);
        getWeekStartDate();
        return getWeekEndDate();
    }

    public String getCurrentWeekEndDate() {
        getCurrentWeekStartDate();
        return getWeekEndDate();
    }

    public String getCurrentWeekDateRange() {
        return getCurrentWeekStartDate() + " - " + getCurrentWeekEndDate();
    }

    public String getWeekRange(String date) {
        setDate(date);
        return getWeekStartDate() + " - " + getWeekEndDate();
    }

    // Given a UserSchedule item, return true if it is in the current week date range
    public boolean isInCurrentWeek(UserSchedule scheduleItem) {
        return scheduleItem.getStartDate().equals(getCurrentWeekStartDate()) &&
                scheduleItem.getEndDate().equals(getCurrentWeekEndDate());
    }

    public boolean isInWeek(String startDate, String endDate, UserSchedule item) {
        return item.getStartDate().equals(startDate) &&
                item.getEndDate().equals(endDate);
    }

    // Given a UserSchedule item, return true if it was marked as completed on today's date
    public boolean completedToday(UserSchedule scheduleItem) {
        return scheduleItem.isCompleted() && scheduleItem.getDateCompleted().equals(getCurrentDate());
    }
}

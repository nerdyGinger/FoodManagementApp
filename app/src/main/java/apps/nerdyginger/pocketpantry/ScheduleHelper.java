package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.models.UserSchedule;

// Helpful methods for dates and date ranges related to scheduling
class ScheduleHelper {
    private Context context;
    private Calendar cal;
    SimpleDateFormat format;


    ScheduleHelper(Context context) {
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

    public String getCurrentWeekStartDate() {
        cal = Calendar.getInstance();
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

    public String getCurrentWeekEndDate() {
        getCurrentWeekStartDate();
        cal.add(Calendar.DAY_OF_YEAR, 6);
        return format.format(cal.getTime());
    }

    public String getCurrentWeekDateRange() {
        return getCurrentWeekStartDate() + " - " + getCurrentWeekEndDate();
    }

    // Given a UserSchedule item, return true if it is in the current week date range
    public boolean isInCurrentWeek(UserSchedule scheduleItem) {
        return scheduleItem.getStartDate().equals(getCurrentWeekStartDate()) &&
                scheduleItem.getEndDate().equals(getCurrentWeekEndDate());
    }

    // Given a UserSchedule item, return true if it was marked as completed on today's date
    public boolean completedToday(UserSchedule scheduleItem) {
        return scheduleItem.isCompleted() && scheduleItem.getDateCompleted().equals(getCurrentDate());
    }
}

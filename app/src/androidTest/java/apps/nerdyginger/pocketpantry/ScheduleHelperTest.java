package apps.nerdyginger.pocketpantry;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import apps.nerdyginger.pocketpantry.helpers.ScheduleHelper;
import apps.nerdyginger.pocketpantry.models.UserSchedule;

// NOTE: update strings for test run date
public class ScheduleHelperTest {
    private ScheduleHelper helper;

    @Before
    public void setup() {
        helper = new ScheduleHelper(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void getCurrentDateTest() {
        Assert.assertEquals("02/13/2020", helper.getCurrentDate());
    }

    @Test
    public void getCurrentWeekStartDateTest() {
        Assert.assertEquals("02/10/2020", helper.getCurrentWeekStartDate());
    }

    @Test
    public void getCurrentWeekEndDateTest() {
        Assert.assertEquals("02/16/2020", helper.getCurrentWeekEndDate());
    }

    @Test
    public void getCurrentWeekDateRangeTest() {
        Assert.assertEquals("02/10/2020 - 02/16/2020", helper.getCurrentWeekDateRange());
    }

    @Test
    public void isInCurrentWeekTest() {
        UserSchedule s1 = new UserSchedule();
        s1.setStartDate("01/16/2004");
        s1.setEndDate("01/18/2004");
        UserSchedule s2 = new UserSchedule();
        s2.setStartDate("02/10/2020");
        s2.setEndDate("02/16/2020");

        Assert.assertFalse(helper.isInCurrentWeek(s1));
        Assert.assertTrue(helper.isInCurrentWeek(s2));
    }

    @Test
    public void getWeekDateRangeTest() {
        Assert.assertEquals("02/10/2020 - 02/16/2020", helper.getWeekRange("02/10/2020"));
        Assert.assertEquals("12/02/2019 - 12/08/2019", helper.getWeekRange("12/04/2019"));
    }
}

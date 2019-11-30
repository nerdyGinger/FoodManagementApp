package apps.nerdyginger.pocketpantry.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.pocketpantry.models.UserSchedule;

@Dao
public interface UserScheduleDao {
    @Insert
    void insert(UserSchedule... userSchedules);

    @Update
    void update(UserSchedule... userSchedules);

    @Delete
    void delete(UserSchedule userSchedule);

    @Query("SELECT * FROM schedule")
    List<UserSchedule> getAllScheduleItems();

    @Query("SELECT * FROM schedule")
    LiveData<List<UserSchedule>> getAllScheduleItemsAsLiveData();

}

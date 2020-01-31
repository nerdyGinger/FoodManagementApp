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

    @Query("SELECT * FROM schedule WHERE recipeBoxItemId = :id")
    UserSchedule getScheduleItemByRecipeBoxId(int id);

    @Query("SELECT * FROM schedule")
    List<UserSchedule> getAllScheduleItems();

    @Query("SELECT * FROM schedule")
    LiveData<List<UserSchedule>> getAllScheduleItemsAsLiveData();

    @Query("SELECT * FROM schedule WHERE completed = 1")
    List<UserSchedule> getAllCompletedScheduleItems();

    @Query("SELECT * FROM schedule WHERE completed = 1")
    LiveData<List<UserSchedule>> getAllCompletedScheduleItemsAsLiveData();

    @Query("SELECT * FROM schedule WHERE completed = 0")
    List<UserSchedule> getAllUncompletedScheduleItems();

    @Query("SELECT * FROM schedule WHERE completed = 0")
    LiveData<List<UserSchedule>> getAllUncompletedScheduleItemsAsLiveData();

}

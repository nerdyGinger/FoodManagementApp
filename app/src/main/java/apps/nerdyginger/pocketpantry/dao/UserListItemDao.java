package apps.nerdyginger.pocketpantry.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.pocketpantry.models.UserListItem;

@Dao
public interface UserListItemDao {
    @Insert
    void insert(UserListItem... items);

    @Update
    void update(UserListItem... items);

    @Delete
    void delete(UserListItem item);

    @Query("SELECT * FROM listItems")
    List<UserListItem> getAllListItems();

    @Query("SELECT * FROM listItems")
    LiveData<List<UserListItem>> getAllListItemsAsLiveData();

    @Query("SELECT * FROM listItems WHERE _ID = :id")
    UserListItem getListItemById(int id);
}

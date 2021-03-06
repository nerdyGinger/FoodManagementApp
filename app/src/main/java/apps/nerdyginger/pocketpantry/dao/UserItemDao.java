package apps.nerdyginger.pocketpantry.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.pocketpantry.models.UserItem;

@Dao
public interface UserItemDao {
    @Insert
    void insert(UserItem... items);

    @Update
    void update(UserItem... items);

    @Delete
    void delete(UserItem item);

    @Query("SELECT name from userItems")
    List<String> getAllItemNames();

    @Query("SELECT _ID from userItems WHERE name = :name")
    int getItemIdFromName(String name);


    @Query("SELECT * FROM userItems")
    List<UserItem> getAllUserItems();

    @Query("SELECT * FROM userItems WHERE _ID = :id")
    UserItem getItemById(int id);
}

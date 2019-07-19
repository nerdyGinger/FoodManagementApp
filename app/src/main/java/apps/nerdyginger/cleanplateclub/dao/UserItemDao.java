package apps.nerdyginger.cleanplateclub.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserItem;

@Dao
public interface UserItemDao {
    @Insert
    void insert(UserItem... items);

    @Update
    void update(UserItem... items);

    @Delete
    void delete(UserItem item);

    @Query("SELECT * FROM userItems")
    List<UserItem> getAllUserItems();

    @Query("SELECT * FROM userItems WHERE _ID = :id")
    UserItem getItemById(int id);
}

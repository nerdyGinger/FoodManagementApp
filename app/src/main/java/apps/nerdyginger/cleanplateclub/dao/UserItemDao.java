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
    public void insert(UserItem... items);

    @Update
    public void update(UserItem... items);

    @Delete
    public void delete(UserItem item);

    @Query("SELECT * FROM userItems")
    public List<UserItem> getAllUserItems();

    @Query("SELECT * FROM userItems WHERE _ID = :id")
    public UserItem getItemById(int id);
}

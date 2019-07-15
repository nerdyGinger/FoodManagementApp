package apps.nerdyginger.cleanplateclub.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserLists;

@Dao
public interface UserListsDao {
    @Insert
    public void insert(UserLists... userLists);

    @Update
    public void update(UserLists... userLists);

    @Delete
    public void delete(UserLists... userList);

    @Query("SELECT * FROM userLists WHERE _ID = :id")
    public UserLists getListById(int id);

    @Query("SELECT * FROM userLists WHERE listName = :name")
    public UserLists getListByName(String name);

    @Query("SELECT listName FROM userLists")
    public List<String> getAllListNames();

    @Query("SELECT * FROM userLists")
    public List<UserLists> getAllLists();

}

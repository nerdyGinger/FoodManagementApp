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
    void insert(UserLists... userLists);

    @Update
    void update(UserLists... userLists);

    @Delete
    void delete(UserLists... userList);

    @Query("SELECT * FROM userLists WHERE _ID = :id")
    UserLists getListById(int id);

    @Query("SELECT * FROM userLists WHERE listName LIKE :name")
    UserLists getListByName(String name);

    @Query("SELECT listName FROM userLists")
    List<String> getAllListNames();

    @Query("SELECT * FROM userLists")
    List<UserLists> getAllLists();

}

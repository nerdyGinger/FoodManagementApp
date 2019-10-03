package apps.nerdyginger.cleanplateclub.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserList;

@Dao
public interface UserListsDao {
    @Insert
    void insert(UserList... userLists);

    @Update
    void update(UserList... userLists);

    @Delete
    void delete(UserList... userList);

    @Query("SELECT * FROM UserList WHERE _ID = :id")
    UserList getListById(int id);

    @Query("SELECT * FROM UserList WHERE listName LIKE :name")
    UserList getListByName(String name);

    @Query("SELECT listName FROM UserList")
    List<String> getAllListNames();

    @Query("SELECT * FROM UserList")
    List<UserList> getAllLists();

}

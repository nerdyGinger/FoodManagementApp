package apps.nerdyginger.pocketpantry.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.pocketpantry.models.UserInventoryItem;

@Dao
public interface UserInventoryItemDao {
    @Insert
    void insert(UserInventoryItem... inventoryItems);

    @Update
    void update(UserInventoryItem... inventoryItems);

    @Delete
    void delete(UserInventoryItem inventoryItem);

    @Query("SELECT * FROM inventory")
    List<UserInventoryItem> getAllInventoryItems();

    @Query("SELECT * FROM inventory")
    LiveData<List<UserInventoryItem>> getAllInventoryItemsAsLiveData();

    @Query("SELECT * FROM inventory WHERE _ID = :id")
    UserInventoryItem getInventoryItemById(int id);

    @Query("SELECT * FROM inventory WHERE itemName LIKE :name")
    UserInventoryItem getInventoryItemByName(String name);

    @Query("SELECT itemName FROM inventory WHERE _ID = :id")
    String getInventoryItemNameById(int id);

    @Query("SELECT _ID FROM inventory WHERE itemName LIKE :name")
    int getInventoryItemIdByName(String name);

    @Query("SELECT quantity FROM inventory WHERE _ID = :id")
    int getInventoryItemQuantity(int id);

    @Query("SELECT * FROM inventory WHERE itemId = :id AND userAdded = :userAdded")
    UserInventoryItem getInventoryItemIdByItemId(int id, boolean userAdded);
}

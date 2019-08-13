package apps.nerdyginger.cleanplateclub.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserInventory;

@Dao
public interface UserInventoryDao {
    @Insert
    void insert(UserInventory... inventoryItems);

    @Update
    void update(UserInventory... inventoryItems);

    @Delete
    void delete(UserInventory inventoryItem);

    @Query("SELECT * FROM inventory")
    List<UserInventory> getAllInventoryItems();

    @Query("SELECT * FROM inventory")
    LiveData<List<UserInventory>> getAllInventoryItemsAsLiveData();

    @Query("SELECT * FROM inventory WHERE _ID = :id")
    UserInventory getInventoryItemById(int id);

    @Query("SELECT * FROM inventory WHERE itemName LIKE :name")
    UserInventory getInventoryItemByName(String name);

    @Query("SELECT itemName FROM inventory WHERE _ID = :id")
    String getInventoryItemNameById(int id);

    @Query("SELECT _ID FROM inventory WHERE itemName LIKE :name")
    int getInventoryItemIdByName(String name);

    @Query("SELECT quantity FROM inventory WHERE _ID = :id")
    int getInventoryItemQuantity(int id);
}

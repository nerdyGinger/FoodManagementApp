package apps.nerdyginger.cleanplateclub.dao;

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
    public void insert(UserInventory... inventoryItems);

    @Update
    public void update(UserInventory... inventoryItems);

    @Delete
    public void delete(UserInventory inventoryItem);

    @Query("SELECT * FROM inventory")
    public List<UserInventory> getAllInventoryItems();

    @Query("SELECT * FROM inventory WHERE _ID = :id")
    public UserInventory getInventoryItemById(int id);

    @Query("SELECT * FROM inventory WHERE itemName = :name")
    public UserInventory getInventoryItemByName(String name);

    @Query("SELECT itemName FROM inventory WHERE _ID = :id")
    public String getInventoryItemNameById(int id);

    @Query("SELECT _ID FROM inventory WHERE itemName = :name")
    public int getInventoryItemIdByName(String name);

    @Query("SELECT quantity FROM inventory WHERE _ID = :id")
    public int getInventoryItemQuantity(int id);
}

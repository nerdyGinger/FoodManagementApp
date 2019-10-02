package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.UserCustomDatabase;
import apps.nerdyginger.cleanplateclub.models.UserInventoryItem;
import apps.nerdyginger.cleanplateclub.models.UserItem;

public class InventoryHelperDao {
    private UserItemDao userItemDao;
    private UserInventoryItemDao inventoryDao;

    public InventoryHelperDao(Context context) {
        UserCustomDatabase customDb = Room.inMemoryDatabaseBuilder(context, UserCustomDatabase.class).build();
        userItemDao = customDb.getUserItemDao();
        inventoryDao = customDb.getUserInventoryDao();
    }

    public List<Integer> getAllItemIds() {
        List<Integer> combineIds = new ArrayList<>();
        combineIds.addAll(getUserItemIds());
        combineIds.addAll(getReadOnlyItemIds());
        return combineIds;
    }

    public List<String> getAllItemNames() {
        List<String> combineNames = new ArrayList<>();
        combineNames.addAll(getUserItemNames());
        combineNames.addAll(getReadOnlyItemNames());
        return combineNames;
    }

    public List<String> getUserItemNames() {
        List<String> justNames = new ArrayList<>();
        List<UserItem> itemObjects = userItemDao.getAllUserItems();
        for (int i=0; i<itemObjects.size(); i++) {
            justNames.add(itemObjects.get(i).getName());
        }
        return justNames;
    }

    public List<String> getReadOnlyItemNames() {
        List<String> justNames = new ArrayList<>();
        List<UserInventoryItem> inventoryItems = inventoryDao.getAllInventoryItems();
        for (int i=0; i<inventoryItems.size(); i++) {
            if ( ! inventoryItems.get(i).isUserAdded()) {
                justNames.add(inventoryItems.get(i).getItemName());
            }
        }
        return justNames;
    }

    public List<Integer> getUserItemIds() {
        List<Integer> justIds =  new ArrayList<>();
        List<UserItem> itemObjects = userItemDao.getAllUserItems();
        for (int i=0; i<itemObjects.size(); i++) {
            justIds.add(itemObjects.get(i).get_ID());
        }
        return justIds;
    }

    public List<Integer> getReadOnlyItemIds() {
        List<Integer> justIds = new ArrayList<>();
        List<UserInventoryItem> inventoryItems = inventoryDao.getAllInventoryItems();
        for (int i=0; i<inventoryItems.size(); i++) {
            if ( ! inventoryItems.get(i).isUserAdded()) {
                justIds.add(inventoryItems.get(i).getItemId());
            }
        }
        return justIds;
    }
}

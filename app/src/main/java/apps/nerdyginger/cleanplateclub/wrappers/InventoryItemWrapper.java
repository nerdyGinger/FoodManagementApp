package apps.nerdyginger.cleanplateclub.wrappers;

import android.content.Context;

import androidx.room.Room;

import java.util.List;

import apps.nerdyginger.cleanplateclub.UserCustomDatabase;
import apps.nerdyginger.cleanplateclub.dao.AllergyItemJoinDao;
import apps.nerdyginger.cleanplateclub.dao.CategoryDao;
import apps.nerdyginger.cleanplateclub.dao.FlavorDao;
import apps.nerdyginger.cleanplateclub.dao.ItemDao;
import apps.nerdyginger.cleanplateclub.dao.UnitDao;
import apps.nerdyginger.cleanplateclub.dao.UnitSystemDao;
import apps.nerdyginger.cleanplateclub.dao.UserInventoryItemDao;
import apps.nerdyginger.cleanplateclub.dao.UserItemDao;
import apps.nerdyginger.cleanplateclub.models.Allergy;
import apps.nerdyginger.cleanplateclub.models.Item;
import apps.nerdyginger.cleanplateclub.models.Unit;
import apps.nerdyginger.cleanplateclub.models.UserInventoryItem;
import apps.nerdyginger.cleanplateclub.models.UserItem;

public class InventoryItemWrapper {
    // This wrapper will grab all of the associated values for an item from the appropriate
    // tables and store them as easily accessible strings. It's a one-stop inventory item shop!

    private String inventoryId;
    private String itemId;
    private String itemName;
    private String unitFullName;
    private String unitAbbreviation;
    private String unitSystem;
    private List<String> itemAllergies;
    private String itemCategory;
    private String itemFlavor;
    private int itemMaxQuantity;
    private int itemQuantity;

    public String getInventoryId() {
        return inventoryId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getUnitFullName() {
        return unitFullName;
    }

    public String getUnitAbbreviation() {
        return unitAbbreviation;
    }

    public String getUnitSystem() {
        return unitSystem;
    }

    public List<String> getItemAllergies() {
        return itemAllergies;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemFlavor() {
        return itemFlavor;
    }

    public int getItemMaxQuantity() {
        return itemMaxQuantity;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemAllergies(List<String> itemAllergies) {
        this.itemAllergies = itemAllergies;
    }

    public InventoryItemWrapper() {
        //empty constructor
    }

    public InventoryItemWrapper (Context context, int inventoryId) {
        UserCustomDatabase customDb = Room.inMemoryDatabaseBuilder(context, UserCustomDatabase.class).build();
        UserInventoryItemDao inventoryDao = customDb.getUserInventoryDao();
        UserInventoryItem inventoryItem = inventoryDao.getInventoryItemById(inventoryId);
        CategoryDao categoryDao = new CategoryDao(context);
        FlavorDao flavorDao = new FlavorDao(context);
        UnitSystemDao systemDao = new UnitSystemDao(context);
        UnitDao unitDao = new UnitDao(context);
        Unit unit = unitDao.getUnitById(inventoryItem.getUnit());
        this.inventoryId = String.valueOf(inventoryId);
        this.itemId = String.valueOf(inventoryItem.getItemId());
        this.itemName = inventoryItem.getItemName();
        this.itemQuantity = inventoryItem.getQuantity();
        this.itemMaxQuantity = inventoryItem.getMaxQuantity();
        this.unitFullName = unit.getFullName();
        this.unitAbbreviation = unit.getAbbreviation();
        this.unitSystem = systemDao.getSystemById(String.valueOf(unit.getSystemId())).getName();
        if (inventoryItem.isUserAdded()) {
            UserItemDao itemDao = customDb.getUserItemDao();
            UserItem item = itemDao.getItemById(inventoryItem.getItemId());
            this.itemCategory = categoryDao.getCategoryName(String.valueOf(item.getCategory()));
            this.itemFlavor = flavorDao.getFlavorName(String.valueOf(item.getFlavor()));
            this.itemAllergies = item.getAllergy();
        } else {
            ItemDao itemDao = new ItemDao(context);
            Item item = itemDao.getItemFromId(String.valueOf(inventoryItem.getItemId()));
            AllergyItemJoinDao allergyItemJoinDao = new AllergyItemJoinDao(context);
            this.itemCategory = categoryDao.getCategoryName(String.valueOf(item.getCategory()));
            this.itemFlavor = flavorDao.getFlavorName(String.valueOf(item.getFlavor()));
            List<Allergy> allergies = allergyItemJoinDao.getAllergiesByItem(itemId);
            for (int i=0; i<allergies.size(); i++) {
                this.itemAllergies.add(allergies.get(i).getName());
            }
        }
    }
}

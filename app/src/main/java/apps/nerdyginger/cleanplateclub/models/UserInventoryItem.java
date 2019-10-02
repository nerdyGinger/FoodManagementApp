package apps.nerdyginger.cleanplateclub.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "inventory")
public class UserInventoryItem {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private boolean userAdded;

    @NonNull
    private int itemId;

    @NonNull
    private String itemName;

    @NonNull
    private boolean quantify;

    private int quantity;

    private String unit;

    @NonNull
    private boolean multiUnit;

    private int maxQuantity;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public boolean isUserAdded() {
        return userAdded;
    }

    public void setUserAdded(boolean userAdded) {
        this.userAdded = userAdded;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @NonNull
    public String getItemName() {
        return itemName;
    }

    public void setItemName(@NonNull String itemName) {
        this.itemName = itemName;
    }

    public boolean isQuantify() {
        return (quantity != 0);
    }

    public void setQuantify(boolean quantify) {
        this.quantify = quantify;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        if (unit == null) {
            unit = "";
        }
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isMultiUnit() {
        return (maxQuantity != 0);
    }

    public void setMultiUnit(boolean multiUnit) {
        this.multiUnit = multiUnit;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}

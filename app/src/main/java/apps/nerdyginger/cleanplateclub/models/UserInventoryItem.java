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

    private String quantity;

    private String unit;

    @NonNull
    private boolean multiUnit;

    private String maxQuantity;

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
        return (!quantity.equals(""));
    }

    public void setQuantify(boolean quantify) {
        this.quantify = quantify;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
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
        return ! (maxQuantity==null || maxQuantity.equals(""));
    }

    public void setMultiUnit(boolean multiUnit) {
        this.multiUnit = multiUnit;
    }

    public String getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(String maxQuantity) {
        this.maxQuantity = maxQuantity;
    }
}

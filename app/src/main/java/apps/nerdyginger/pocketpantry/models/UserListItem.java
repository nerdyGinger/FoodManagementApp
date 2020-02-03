package apps.nerdyginger.pocketpantry.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "listItems")
public class UserListItem {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private String itemName;
    private String quantity;
    private boolean userAdded;
    private int itemID;
    private boolean checked;

    //for sorting into multiple lists; store list ids in preferences? or make another db table?
    private int listID;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isUserAdded() {
        return userAdded;
    }

    public void setUserAdded(boolean userAdded) {
        this.userAdded = userAdded;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }
}

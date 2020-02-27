package apps.nerdyginger.pocketpantry.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "listItems")
public class UserListItem implements Comparable<UserListItem> {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private String itemName;
    private String quantity;
    private boolean userAdded;
    private int itemID;
    private boolean checked;
    private String notes;
    private boolean expanded = false;

    // this indicated the item's position in the list, which is necessary for persistent sorting
    private int position;

    //for sorting into multiple lists; store list ids in preferences? or make another db table?
    //TODO: Multiple lists? Ver. 2?
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int compareTo(UserListItem compareItem) {
        int compareQuantity = compareItem.getPosition();
        return position - compareQuantity;
    }
}

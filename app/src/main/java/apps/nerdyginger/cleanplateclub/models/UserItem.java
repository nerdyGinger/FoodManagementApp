package apps.nerdyginger.cleanplateclub.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userItems")
public class UserItem {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private String name;
    private int flavor;
    private int category;
    private int allergy;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlavor() {
        return flavor;
    }

    public void setFlavor(int flavor) {
        this.flavor = flavor;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getAllergy() {
        return allergy;
    }

    public void setAllergy(int allergy) {
        this.allergy = allergy;
    }
}

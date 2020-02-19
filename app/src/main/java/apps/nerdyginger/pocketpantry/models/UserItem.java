package apps.nerdyginger.pocketpantry.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "userItems")
public class UserItem {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private String name;

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

}

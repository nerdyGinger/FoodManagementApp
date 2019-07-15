package apps.nerdyginger.cleanplateclub.models;

import android.content.Context;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.UserCustomDatabase;
import apps.nerdyginger.cleanplateclub.dao.UserItemDao;

@Entity(tableName = "userLists")
public class UserLists {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    private String listName;
    private String items; //item ids are stored here with ;;; separator

    private UserCustomDatabase database;

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    //parse db item ids string into a list of UserItems
    public List<UserItem> getItemsList(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context, UserCustomDatabase.class, "userCustomDb")
                    .allowMainThreadQueries()
                    .build();
        }
        UserItemDao dao = database.getUserItemDao();
        List<UserItem> itemsList = new ArrayList<>();
        String[] split = items.split(";;;");
        try {
            for (String s : split) {
                itemsList.add(dao.getItemById(Integer.parseInt(s)));
            }
        } catch (Exception e) {
            Log.e("Items parsing error", e.toString());
        }
        return itemsList;
    }
}

package apps.nerdyginger.pocketpantry.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "userRecipeItemJoin")
public class UserRecipeItemJoin {
    @PrimaryKey(autoGenerate = true)
    private int _ID;

    public int recipeId; //unofficial foreign keys (from recipeBox)
    public int itemId;   //        '' ''           (from inventory or read-only db)
    public String quantity;
    public String unit;
    public String detail;

    //ONLY in user db
    public String itemName; //because what if it's not in the db? Still allow user to input recipe ingredients
                            //also will offload some db functions (all user recipes add this value, if not in
                            //db, then itemId is set to -1).


    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }
}

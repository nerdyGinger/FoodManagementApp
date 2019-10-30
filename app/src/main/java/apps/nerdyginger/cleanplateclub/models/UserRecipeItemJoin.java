package apps.nerdyginger.cleanplateclub.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "userRecipeItemJoin",
        primaryKeys = {"recipeId", "itemId"})
public class UserRecipeItemJoin {
    public int recipeId; //unofficial foreign keys
    public int itemId;   //        '' ''
    public String quantity;
    public String unit;
    public String detail;

    //ONLY in user db
    public String itemName; //because what if it's not in the db? Still allow user to input recipe ingredients
                            //also will offload some db functions (all user recipes add this value, if not in
                            //db, then itemId is set to -1).
}

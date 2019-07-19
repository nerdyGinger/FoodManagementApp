package apps.nerdyginger.cleanplateclub.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "userRecipeItemJoin",
        primaryKeys = {"recipeId", "itemId"},
        foreignKeys = {
            @ForeignKey(entity = UserRecipe.class,
                        parentColumns = "_ID",
                        childColumns = "recipeId"),
            @ForeignKey(entity = UserItem.class,
                        parentColumns = "_ID",
                        childColumns =  "itemId")
        })
public class UserRecipeItemJoin {
    public int recipeId;
    public int itemId;
}

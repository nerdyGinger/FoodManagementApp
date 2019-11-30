package apps.nerdyginger.pocketpantry.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserRecipe;
import apps.nerdyginger.pocketpantry.models.UserRecipeItemJoin;

@Dao
public interface UserRecipeItemJoinDao {
    @Insert
    void insert(UserRecipeItemJoin userRecipeItemJoin);

    @Delete
    void delete(UserRecipeItemJoin userRecipeItemJoin);

    @Query("SELECT * FROM userRecipes " +
            "INNER JOIN userRecipeItemJoin " +
            "ON userRecipes._ID=userRecipeItemJoin.recipeId " +
            "WHERE userRecipeItemJoin.itemId=:itemId")
    List<UserRecipe> getRecipesByItem(int itemId);

    @Query("SELECT * FROM inventory " +
            "INNER JOIN userRecipeItemJoin " +
            "ON inventory.itemId=userRecipeItemJoin.itemId " +
            "WHERE userRecipeItemJoin.recipeId=:recipeId")
    List<UserInventoryItem> getItemsInRecipe(int recipeId);

    @Query("SELECT * FROM userRecipeItemJoin " +
           "WHERE recipeId=:recipeId")
    List<UserRecipeItemJoin> getJoinItemsInRecipe(int recipeId);
}

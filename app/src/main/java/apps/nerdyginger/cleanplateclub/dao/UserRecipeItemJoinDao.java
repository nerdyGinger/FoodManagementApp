package apps.nerdyginger.cleanplateclub.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserItem;
import apps.nerdyginger.cleanplateclub.models.UserRecipe;
import apps.nerdyginger.cleanplateclub.models.UserRecipeItemJoin;

@Dao
public interface UserRecipeItemJoinDao {
    @Insert
    void insert(UserRecipeItemJoin userRecipeItemJoin);

    @Query("SELECT * FROM userRecipes " +
            "INNER JOIN userRecipeItemJoin " +
            "ON userRecipes._ID=userRecipeItemJoin.recipeId " +
            "WHERE userRecipeItemJoin.itemId=:itemId")
    List<UserRecipe> getRecipesByItem(int itemId);

    @Query("SELECT * FROM userItems " +
            "INNER JOIN userRecipeItemJoin " +
            "ON userItems._ID=userRecipeItemJoin.itemId " +
            "WHERE userRecipeItemJoin.recipeId=:recipeId")
    List<UserItem> getItemsInRecipe(int recipeId);

    @Query("SELECT * FROM userRecipeItemJoin " +
           "WHERE recipeId=:recipeId")
    List<UserRecipeItemJoin> getJoinItemsInRecipe(int recipeId);
}

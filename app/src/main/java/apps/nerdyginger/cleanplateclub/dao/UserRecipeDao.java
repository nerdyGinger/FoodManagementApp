package apps.nerdyginger.cleanplateclub.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserRecipe;

@Dao
public interface UserRecipeDao {
    //will return generated id
    @Insert
    long[] insert(UserRecipe... recipes);

    @Update
    void update(UserRecipe... reicpes);

    @Delete
    void delete(UserRecipe recipe);

    @Query("SELECT * FROM userRecipes")
    List<UserRecipe> getAllUserRecipes();

    @Query("SELECT * FROM userRecipes")
    LiveData<List<UserRecipe>> getAllUserRecipesAsLiveData();

    @Query("SELECT * FROM userRecipes WHERE _ID = :id")
    UserRecipe getUserRecipeById(int id);
}

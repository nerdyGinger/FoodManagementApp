package apps.nerdyginger.cleanplateclub.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserRecipeBoxItem;

@Dao
public interface UserRecipeBoxDao {
    @Insert
    void insert(UserRecipeBoxItem... recipes);

    @Update
    void update(UserRecipeBoxItem... recipes);

    @Delete
    void delete(UserRecipeBoxItem recipe);

    @Query("Select * from recipeBox")
    List<UserRecipeBoxItem> getAllRecipes();

    @Query("Select * from recipeBox")
    LiveData<List<UserRecipeBoxItem>> getAllRecipesAsLiveData();

    @Query("Select * from recipeBox where _ID = :id")
    UserRecipeBoxItem getRecipeById(int id);
}

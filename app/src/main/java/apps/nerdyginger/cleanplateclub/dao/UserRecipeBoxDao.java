package apps.nerdyginger.cleanplateclub.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserRecipeBox;

@Dao
public interface UserRecipeBoxDao {
    @Insert
    void insert(UserRecipeBox... recipes);

    @Update
    void update(UserRecipeBox... recipes);

    @Delete
    void delete(UserRecipeBox recipe);

    @Query("Select * from recipeBox")
    List<UserRecipeBox> getAllRecipes();

    @Query("Select * from recipeBox")
    LiveData<List<UserRecipeBox>> getAllRecipesAsLiveData();

    @Query("Select * from recipeBox where _ID = :id")
    UserRecipeBox getRecipeById(int id);
}

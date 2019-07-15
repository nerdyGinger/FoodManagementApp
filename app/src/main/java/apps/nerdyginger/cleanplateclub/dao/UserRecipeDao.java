package apps.nerdyginger.cleanplateclub.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserRecipe;

@Dao
public interface UserRecipeDao {
    @Insert
    public void insert(UserRecipe... recipes);

    @Update
    public void update(UserRecipe... reicpes);

    @Delete
    public void delete(UserRecipe recipe);

    @Query("SELECT * FROM userRecipes")
    public List<UserRecipe> getAllUserRecipes();

    @Query("SELECT * FROM userRecipes WHERE _ID = :id")
    public UserRecipe getUserRecipeById(int id);
}

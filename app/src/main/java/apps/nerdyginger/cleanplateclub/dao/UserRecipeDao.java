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
    void insert(UserRecipe... recipes);

    @Update
    void update(UserRecipe... reicpes);

    @Delete
    void delete(UserRecipe recipe);

    @Query("SELECT * FROM userRecipes")
    List<UserRecipe> getAllUserRecipes();

    @Query("SELECT * FROM userRecipes WHERE _ID = :id")
    UserRecipe getUserRecipeById(int id);
}

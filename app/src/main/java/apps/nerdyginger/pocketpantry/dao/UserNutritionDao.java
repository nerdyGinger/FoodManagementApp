package apps.nerdyginger.pocketpantry.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.pocketpantry.models.UserNutrition;

@Dao
public interface UserNutritionDao {
    @Insert
    void insert(UserNutrition... nutritions);

    @Update
    void update(UserNutrition... nutritions);

    @Delete
    void delete(UserNutrition nutrition);

    @Query("SELECT * FROM userNutrition")
    List<UserNutrition> getAllUserNutrition();

    @Query("SELECT * FROM userNutrition WHERE _ID = :id")
    UserNutrition getUserNutritionById(int id);

    @Query("SELECT _ID FROM userNutrition WHERE recipeName LIKE :name")
    int getNutritionIdByName(String name);

    @Query("SELECT recipeName FROM userNutrition WHERE _ID = :id")
    String getNutritionRecipeNameById(int id);
}

package apps.nerdyginger.cleanplateclub.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.nerdyginger.cleanplateclub.models.UserNutrition;

@Dao
public interface UserNutritionDao {
    @Insert
    public void insert(UserNutrition... nutritions);

    @Update
    public void update(UserNutrition... nutritions);

    @Delete
    public void delete(UserNutrition nutrition);

    @Query("SELECT * FROM userNutrition")
    public List<UserNutrition> getAllUserNutrition();

    @Query("SELECT * FROM userNutrition WHERE _ID = :id")
    public UserNutrition getUserNutritionById(int id);

    @Query("SELECT _ID FROM userNutrition WHERE recipeName = :name")
    public int getNutritionIdByName(String name);

    @Query("SELECT recipeName FROM userNutrition WHERE _ID = :id")
    public String getNutritionRecipeNameById(int id);
}

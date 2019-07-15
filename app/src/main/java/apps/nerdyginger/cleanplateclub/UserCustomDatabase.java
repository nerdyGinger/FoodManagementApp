package apps.nerdyginger.cleanplateclub;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import apps.nerdyginger.cleanplateclub.dao.UserItemDao;
import apps.nerdyginger.cleanplateclub.dao.UserNutritionDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeDao;
import apps.nerdyginger.cleanplateclub.models.UserItem;
import apps.nerdyginger.cleanplateclub.models.UserNutrition;
import apps.nerdyginger.cleanplateclub.models.UserRecipe;

@Database(entities = {UserItem.class, UserRecipe.class, UserNutrition.class}, version = 1)
public abstract class UserCustomDatabase extends RoomDatabase {
    public abstract UserItemDao getUserItemDao();
    public abstract UserRecipeDao getUserRecipeDao();
    public abstract UserNutritionDao getUserNutritionDao();
}

package apps.nerdyginger.cleanplateclub;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import apps.nerdyginger.cleanplateclub.dao.UserInventoryDao;
import apps.nerdyginger.cleanplateclub.dao.UserItemDao;
import apps.nerdyginger.cleanplateclub.dao.UserNutritionDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeDao;
import apps.nerdyginger.cleanplateclub.dao.UserRecipeItemJoinDao;
import apps.nerdyginger.cleanplateclub.models.UserInventory;
import apps.nerdyginger.cleanplateclub.models.UserItem;
import apps.nerdyginger.cleanplateclub.models.UserNutrition;
import apps.nerdyginger.cleanplateclub.models.UserRecipe;
import apps.nerdyginger.cleanplateclub.models.UserRecipeItemJoin;

@Database(entities = {UserItem.class, UserRecipe.class, UserNutrition.class, UserInventory.class, UserRecipeItemJoin.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class UserCustomDatabase extends RoomDatabase {
    public abstract UserItemDao getUserItemDao();
    public abstract UserRecipeDao getUserRecipeDao();
    public abstract UserNutritionDao getUserNutritionDao();
    public abstract UserInventoryDao getUserInventoryDao();
    public abstract UserRecipeItemJoinDao getUserRecipeItemJoinDao();
}

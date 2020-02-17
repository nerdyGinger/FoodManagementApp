package apps.nerdyginger.pocketpantry;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserItemDao;
import apps.nerdyginger.pocketpantry.dao.UserListItemDao;
import apps.nerdyginger.pocketpantry.dao.UserNutritionDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeBoxDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeItemJoinDao;
import apps.nerdyginger.pocketpantry.dao.UserScheduleDao;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserItem;
import apps.nerdyginger.pocketpantry.models.UserListItem;
import apps.nerdyginger.pocketpantry.models.UserNutrition;
import apps.nerdyginger.pocketpantry.models.UserRecipe;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;
import apps.nerdyginger.pocketpantry.models.UserRecipeItemJoin;
import apps.nerdyginger.pocketpantry.models.UserSchedule;

@Database(entities = {UserItem.class, UserRecipe.class, UserNutrition.class, UserInventoryItem.class, UserRecipeItemJoin.class,
                        UserRecipeBoxItem.class, UserSchedule.class, UserListItem.class }, version = 16)
@TypeConverters({Converters.class})
public abstract class UserCustomDatabase extends RoomDatabase {
    public abstract UserItemDao getUserItemDao();
    public abstract UserRecipeDao getUserRecipeDao();
    public abstract UserNutritionDao getUserNutritionDao();
    public abstract UserInventoryItemDao getUserInventoryDao();
    public abstract UserRecipeItemJoinDao getUserRecipeItemJoinDao();
    public abstract UserRecipeBoxDao getUserRecipeBoxDao();
    public abstract UserScheduleDao getUserScheduleDao();
    public abstract UserListItemDao getUserListItemDao();
    //public abstract UserList getUserLists();
    private static UserCustomDatabase INSTANCE;

    public static UserCustomDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UserCustomDatabase.class, "userDatabase")
                    .fallbackToDestructiveMigration() //don't do in production!
                    .build();
        }
        return INSTANCE;
    }
}

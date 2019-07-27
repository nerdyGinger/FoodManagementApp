package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.Item;
import apps.nerdyginger.cleanplateclub.models.Recipe;

public class RecipeItemJoinDao {
    private Context context;

    public RecipeItemJoinDao(Context context) {
        this.context = context;
    }

    public List<Recipe> getRecipesByItem(String itemId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Recipe INNER JOIN RecipeItemJoin ON Recipe._ID=RecipeItemJoin.recipeId WHERE RecipeItemJoin.itemId = ?";
        String[] params = new String[] {itemId};
        List<Recipe> recipes = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, params);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                RecipeDao recipeDao = new RecipeDao(context);
                while (!cursor.isAfterLast()) {
                    Integer id = cursor.getInt(cursor.getColumnIndex("recipeId"));
                    recipes.add(recipeDao.buildRecipeFromId(id.toString()));
                }
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return recipes;
    }

    public List<Item> getItemsInRecipe(String recipeId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "SELECT * FROM Item INNER JOIN RecipeItemJoin ON Item._ID=RecipeItemJoin.itemId WHERE RecipeItemJoin.recipeId = ?";
        String[] params = new String[] {recipeId};
        List<Item> items = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, params);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ItemDao itemDao = new ItemDao(context);
                while (!cursor.isAfterLast()) {
                    Integer id = cursor.getInt(cursor.getColumnIndex("itemId"));
                    items.add(itemDao.buildItemFromId(id.toString()));
                }
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return items;
    }
}

package apps.nerdyginger.pocketpantry.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.helpers.DatabaseHelper;
import apps.nerdyginger.pocketpantry.models.Item;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.RecipeItemJoin;

public class RecipeItemJoinDao {
    private Context context;

    public RecipeItemJoinDao(Context context) {
        this.context = context;
    }

    public List<RecipeItemJoin> getJoinItemsByItem(String itemId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from RecipeItemJoin WHERE RecipeItemJoin.itemId = ?";
        String[] params = new String[] {itemId};
        List<RecipeItemJoin> joinItems = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, params);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int recipeId = cursor.getInt(cursor.getColumnIndex("recipeId"));
                    String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                    String unit = cursor.getString(cursor.getColumnIndex("unit"));
                    String detail = cursor.getString(cursor.getColumnIndex("detail"));
                    joinItems.add(new RecipeItemJoin(recipeId, Integer.parseInt(itemId), quantity, unit, detail));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e("JoinItem DB Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return joinItems;
    }

    public List<Recipe> getRecipesByItem(String itemId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Recipe INNER JOIN RecipeItemJoin ON Recipe.rowid=RecipeItemJoin.recipeId WHERE RecipeItemJoin.itemId = ?";
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
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e("JoinItem DB Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return recipes;
    }

    public List<Item> getItemsInRecipe(String recipeId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "SELECT * FROM Item INNER JOIN RecipeItemJoin ON Item.rowid=RecipeItemJoin.itemId WHERE RecipeItemJoin.recipeId = ?";
        String[] params = new String[] {recipeId};
        List<Item> items = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, params);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ItemDao itemDao = new ItemDao(context);
                while (!cursor.isAfterLast()) {
                    Integer id = cursor.getInt(cursor.getColumnIndex("itemId"));
                    items.add(itemDao.getItemFromId(id.toString()));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e("JoinItem DB Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return items;
    }

    public List<RecipeItemJoin> getJoinItemsInRecipe(String recipeId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "SELECT * FROM RecipeItemJoin WHERE RecipeItemJoin.recipeId = ?";
        String[] params = new String[] {recipeId};
        List<RecipeItemJoin> items = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, params);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ItemDao itemDao = new ItemDao(context);
                while (!cursor.isAfterLast()) {
                    Integer itemId = cursor.getInt(cursor.getColumnIndex("itemId"));
                    //String name = cursor.getString(cursor.getColumnIndex("itemName"));                    //TODO: add itemName to db
                    String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                    String unit = cursor.getString(cursor.getColumnIndex("unit"));
                    String detail = cursor.getString(cursor.getColumnIndex("detail"));
                    items.add(new RecipeItemJoin(Integer.parseInt(recipeId), itemId, quantity, unit, detail));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e("JoinItem DB Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return items;
    }
}

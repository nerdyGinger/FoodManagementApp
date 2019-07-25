package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.Recipe;

public class RecipeDao {
    private Context context;

    public RecipeDao(Context context) {
        this.context = context;
    }

    public Recipe buildRecipeFromId(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Recipes where _ID = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        String name = "", author = "", datePublished = "", description = "", totalTime = "", keywords = "";
        String recipeYield = "", recipeCategory = "", recipeCuisine = "", recipeInstructions = "";
        int nutritionId = 0;
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            name = cursor.getString(cursor.getColumnIndex("name"));
            author = cursor.getString(cursor.getColumnIndex("author"));
            description = cursor.getString(cursor.getColumnIndex("description"));
            totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
            keywords = cursor.getString(cursor.getColumnIndex("keywords"));
            recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
            recipeCategory = cursor.getString(cursor.getColumnIndex("recipeCategory"));
            recipeCuisine = cursor.getString(cursor.getColumnIndex("recipeCuisine"));
            nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
            recipeInstructions = cursor.getString(cursor.getColumnIndex("recipeInstructions"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return new Recipe(Integer.parseInt(id), name, author, datePublished, description, totalTime,
                keywords, recipeYield, recipeCategory, recipeCuisine, nutritionId, recipeInstructions);
    }

    public List<Recipe> buildRecipeListFromDb() {
        return getAllRecipes();
    }

    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Recipes", new String[] {});
        List<Recipe> recipes = new ArrayList<>();
        String name = "", author = "", datePublished = "", description = "", totalTime = "", keywords = "";
        String recipeYield = "", recipeCategory = "", recipeCuisine = "", recipeInstructions = "";
        int nutritionId = 0;
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("_ID"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                author = cursor.getString(cursor.getColumnIndex("author"));
                datePublished = cursor.getString(cursor.getColumnIndex("datePublished"));
                description = cursor.getString(cursor.getColumnIndex("description"));
                totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
                keywords = cursor.getString(cursor.getColumnIndex("keywords"));
                recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
                recipeCategory = cursor.getString(cursor.getColumnIndex("recipeCategory"));
                recipeCuisine = cursor.getString(cursor.getColumnIndex("recipeCuisine"));
                nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
                recipeInstructions = cursor.getString(cursor.getColumnIndex("recipeInstructions"));
                recipes.add(new Recipe(id, name, author, datePublished, description, totalTime, keywords, recipeYield, recipeCategory,
                        recipeCuisine, nutritionId, recipeInstructions));
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return recipes;
    }

    private String runQuerySingle(String sql, String[] params, String column) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, params);
        String output = "";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            output = cursor.getString(cursor.getColumnIndex(column));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return output;
    }

    public String getRecipeId(String name) {
        String sql = "Select _ID from Recipes where name = ?";
        return runQuerySingle(sql, new String[] {name}, "_ID");
    }

    public String getRecipeName(String id) {
        String sql = "Select name from Recipes where _ID = ?";
        return runQuerySingle(sql, new String[] {id}, "name");
    }
}

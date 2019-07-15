package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String author = cursor.getString(cursor.getColumnIndex("author"));
        String datePublished = cursor.getString(cursor.getColumnIndex("datePublished"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        String totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
        String keywords = cursor.getString(cursor.getColumnIndex("keywords"));
        String recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
        String recipeCategory = cursor.getString(cursor.getColumnIndex("recipeCategory"));
        String recipeCuisine = cursor.getString(cursor.getColumnIndex("recipeCuisine"));
        int nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
        String recipeIngredient = cursor.getString(cursor.getColumnIndex("recipeIngredient"));
        String recipeInstructions = cursor.getString(cursor.getColumnIndex("recipeInstructions"));
        cursor.close();
        return new Recipe(Integer.parseInt(id), name, author, datePublished, description, totalTime,
                keywords, recipeYield, recipeCategory, recipeCuisine, nutritionId, recipeIngredient, recipeInstructions);
    }

    public List<Recipe> buildRecipeListFromDb() {
        return getAllRecipes();
    }

    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Recipes", new String[] {});
        cursor.moveToFirst();
        List<Recipe> recipes = new ArrayList<>();
        while ( !cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex("_ID"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String author = cursor.getString(cursor.getColumnIndex("author"));
            String datePublished = cursor.getString(cursor.getColumnIndex("datePublished"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            String totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
            String keywords = cursor.getString(cursor.getColumnIndex("keywords"));
            String recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
            String recipeCategory = cursor.getString(cursor.getColumnIndex("recipeCategory"));
            String recipeCuisine = cursor.getString(cursor.getColumnIndex("recipeCuisine"));
            int nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
            String recipeIngredient = cursor.getString(cursor.getColumnIndex("recipeIngredient"));
            String recipeInstructions = cursor.getString(cursor.getColumnIndex("recipeInstructions"));
            recipes.add(new Recipe(id, name, author, datePublished, description, totalTime, keywords, recipeYield, recipeCategory,
                    recipeCuisine, nutritionId, recipeIngredient, recipeInstructions));
        }
        cursor.close();
        return recipes;
    }

    private String runQuerySingle(String sql, String[] params, String column) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, params);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        String output = cursor.getString(cursor.getColumnIndex(column));
        cursor.close();
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

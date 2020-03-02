package apps.nerdyginger.pocketpantry.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.Converters;
import apps.nerdyginger.pocketpantry.helpers.DatabaseHelper;
import apps.nerdyginger.pocketpantry.models.Recipe;

public class RecipeDao {
    private Context context;

    public RecipeDao(Context context) {
        this.context = context;
    }

    public List<Recipe> getAllRecipesByCuisine(String cuisineId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Recipes where recipeCuisine = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {cuisineId});
        String name, author, datePublished, description, totalTime, recipeYield, recipeCategory;
        String url, recipeBookId, imageUrl;
        ArrayList<String> keywords = new ArrayList<>();
        ArrayList<String> recipeInstructions = new ArrayList<>();
        int nutritionId, id;
        List<Recipe> recipes = new ArrayList<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            while ( ! cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("rowid"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                author = cursor.getString(cursor.getColumnIndex("author"));
                url = cursor.getString(cursor.getColumnIndex("url"));
                recipeBookId = cursor.getString(cursor.getColumnIndex("recipeBookId"));
                datePublished = cursor.getString(cursor.getColumnIndex("datePublished"));
                description = cursor.getString(cursor.getColumnIndex("description"));
                totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
                keywords = Converters.fromString(cursor.getString(cursor.getColumnIndex("keywords")));
                recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
                recipeCategory = cursor.getString(cursor.getColumnIndex("recipeCategory"));
                nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
                imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                recipeInstructions = Converters.fromString(cursor.getString(cursor.getColumnIndex("recipeInstructions")));
                recipes.add(new Recipe(id, name, author, url, recipeBookId, datePublished, description, totalTime, keywords, recipeYield, recipeCategory,
                        cuisineId, imageUrl, nutritionId, recipeInstructions));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return recipes;
    }

    public List<Recipe> getAllRecipesByCategory(String categoryId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Recipes where recipeCategory = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {categoryId});
        String name, author, datePublished, description, totalTime, recipeYield, recipeCuisine;
        String url, recipeBookId, imageUrl;
        ArrayList<String> keywords = new ArrayList<>();
        ArrayList<String> recipeInstructions = new ArrayList<>();
        int nutritionId, id;
        List<Recipe> recipes = new ArrayList<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            while ( ! cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("rowid"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                author = cursor.getString(cursor.getColumnIndex("author"));
                url = cursor.getString(cursor.getColumnIndex("url"));
                recipeBookId = cursor.getString(cursor.getColumnIndex("recipeBookId"));
                datePublished = cursor.getString(cursor.getColumnIndex("datePublished"));
                description = cursor.getString(cursor.getColumnIndex("description"));
                totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
                keywords = Converters.fromString(cursor.getString(cursor.getColumnIndex("keywords")));
                recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
                recipeCuisine = cursor.getString(cursor.getColumnIndex("recipeCuisine"));
                nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
                imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                recipeInstructions = Converters.fromString(cursor.getString(cursor.getColumnIndex("recipeInstructions")));
                recipes.add(new Recipe(id, name, author, url, recipeBookId, datePublished, description, totalTime, keywords, recipeYield, categoryId,
                        recipeCuisine, imageUrl, nutritionId, recipeInstructions));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return recipes;
    }

    public Recipe buildRecipeFromId(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Recipes where rowid = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        String name = "", author = "", datePublished = "", description = "", totalTime = "";
        String url = "", recipeBookId = "", imageUrl = "";
        ArrayList<String> keywords = new ArrayList<>();
        String recipeYield = "", recipeCategory = "", recipeCuisine = "";
        ArrayList<String> recipeInstructions = new ArrayList<>();
        int nutritionId = 0;
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            name = cursor.getString(cursor.getColumnIndex("name"));
            author = cursor.getString(cursor.getColumnIndex("author"));
            url = cursor.getString(cursor.getColumnIndex("url"));
            recipeBookId = cursor.getString(cursor.getColumnIndex("recipeBookId"));
            description = cursor.getString(cursor.getColumnIndex("description"));
            totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
            keywords = Converters.fromString(cursor.getString(cursor.getColumnIndex("keywords")));
            recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
            recipeCategory = cursor.getString(cursor.getColumnIndex("recipeCategory"));
            recipeCuisine = cursor.getString(cursor.getColumnIndex("recipeCuisine"));
            nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
            imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
            recipeInstructions = Converters.fromString(cursor.getString(cursor.getColumnIndex("recipeInstructions")));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return new Recipe(Integer.parseInt(id), name, author, url, recipeBookId, datePublished, description, totalTime,
                keywords, recipeYield, recipeCategory, recipeCuisine, imageUrl, nutritionId, recipeInstructions);
    }

    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Recipes", new String[] {});
        List<Recipe> recipes = new ArrayList<>();
        String name, author, datePublished, description, totalTime, recipeYield, recipeCategory, recipeCuisine;
        String url, recipeBookId, imageUrl;
        ArrayList<String> keywords, recipeInstructions;
        int nutritionId;
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("rowid"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                author = cursor.getString(cursor.getColumnIndex("author"));
                url = cursor.getString(cursor.getColumnIndex("url"));
                recipeBookId = cursor.getString(cursor.getColumnIndex("recipeBookId"));
                datePublished = cursor.getString(cursor.getColumnIndex("datePublished"));
                description = cursor.getString(cursor.getColumnIndex("description"));
                totalTime = cursor.getString(cursor.getColumnIndex("totalTime"));
                keywords = Converters.fromString(cursor.getString(cursor.getColumnIndex("keywords")));
                recipeYield = cursor.getString(cursor.getColumnIndex("recipeYield"));
                recipeCategory = cursor.getString(cursor.getColumnIndex("recipeCategory"));
                recipeCuisine = cursor.getString(cursor.getColumnIndex("recipeCuisine"));
                nutritionId = cursor.getInt(cursor.getColumnIndex("nutrition"));
                imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                recipeInstructions = Converters.fromString(cursor.getString(cursor.getColumnIndex("recipeInstructions")));
                recipes.add(new Recipe(id, name, author, url, recipeBookId, datePublished, description, totalTime, keywords, recipeYield, recipeCategory,
                        recipeCuisine, imageUrl, nutritionId, recipeInstructions));
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
        return runQuerySingle(sql, new String[] {name}, "rowid");
    }

    public String getRecipeName(String id) {
        String sql = "Select name from Recipes where rowid = ?";
        return runQuerySingle(sql, new String[] {id}, "name");
    }
}

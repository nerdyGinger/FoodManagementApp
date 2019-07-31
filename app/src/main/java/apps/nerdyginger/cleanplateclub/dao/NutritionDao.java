package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.Nutrition;

public class NutritionDao {
    private Context context;

    public NutritionDao(Context context) {
        this.context = context;
    }

    public Nutrition buildNutritionFromId(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Nutrition where rowid = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        String name = "", servingSize = "", calories = "", sugarContent = "", sodiumContent = "", fatContent = "";
        String saturatedFat = "", transFat = "", carbs = "", fiber = "", protein = "", cholesterol = "";
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            name = cursor.getString(cursor.getColumnIndex("recipeName"));
            servingSize = cursor.getString(cursor.getColumnIndex("servingSize"));
            calories = cursor.getString(cursor.getColumnIndex("calories"));
            sugarContent = cursor.getString(cursor.getColumnIndex("sugarContent"));
            sodiumContent = cursor.getString(cursor.getColumnIndex("sodiumContent"));
            fatContent = cursor.getString(cursor.getColumnIndex("fatContent"));
            saturatedFat = cursor.getString(cursor.getColumnIndex("saturatedFatContent"));
            transFat = cursor.getString(cursor.getColumnIndex("transFatContent"));
            carbs = cursor.getString(cursor.getColumnIndex("carbohydrateContent"));
            fiber = cursor.getString(cursor.getColumnIndex("fiberContent"));
            protein = cursor.getString(cursor.getColumnIndex("proteinContent"));
            cholesterol = cursor.getString(cursor.getColumnIndex("cholesterolContent"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return new Nutrition(Integer.parseInt(id), name, servingSize, calories, sugarContent, sodiumContent,
                fatContent, saturatedFat, transFat, carbs, fiber, protein, cholesterol);
    }

    public List<Nutrition> buildNutritionListFromDb() {
        return getAllNutritions();
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

    //Should use buildNutritionListFromDb for all outside uses
    //I know that's bad grammar; LOL just me here though ^u^'
    public List<Nutrition> getAllNutritions() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Nutrition", new String[] {});
        List<Nutrition> nutritions = new ArrayList<>();
        String name = "", servingSize = "", calories = "", sugarContent = "", sodiumContent = "", fatContent = "";
        String saturatedFat = "", transFat = "", carbs = "", fiber = "", protein = "", cholesterol = "";
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("rowid"));
                name = cursor.getString(cursor.getColumnIndex("recipeName"));
                servingSize = cursor.getString(cursor.getColumnIndex("servingSize"));
                calories = cursor.getString(cursor.getColumnIndex("calories"));
                sugarContent = cursor.getString(cursor.getColumnIndex("sugarContent"));
                sodiumContent = cursor.getString(cursor.getColumnIndex("sodiumContent"));
                fatContent = cursor.getString(cursor.getColumnIndex("fatContent"));
                saturatedFat = cursor.getString(cursor.getColumnIndex("saturatedFatContent"));
                transFat = cursor.getString(cursor.getColumnIndex("transFatContent"));
                carbs = cursor.getString(cursor.getColumnIndex("carbohydrateContent"));
                fiber = cursor.getString(cursor.getColumnIndex("fiberContent"));
                protein = cursor.getString(cursor.getColumnIndex("proteinContent"));
                cholesterol = cursor.getString(cursor.getColumnIndex("cholesterolContent"));
                nutritions.add(new Nutrition(id, name, servingSize, calories, sugarContent, sodiumContent, fatContent, saturatedFat,
                        transFat, carbs, fiber, protein, cholesterol));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return nutritions;
    }

    public String getNutritionName(String id) {
        String sql = "SELECT recipeName FROM Nutrition WHERE rowid = ?";
        return runQuerySingle(sql, new String[] {id}, "recipeName");
    }

    public String getNutritionId(String name) {
        String sql = "SELECT id FROM Nutrition WHERE recipeName = ?";
        return runQuerySingle(sql, new String[] {name}, "rowid");
    }
}

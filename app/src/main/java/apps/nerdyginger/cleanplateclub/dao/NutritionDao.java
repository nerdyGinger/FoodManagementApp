package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        String sql = "Select * from Nutrition where _ID = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        String name = cursor.getString(cursor.getColumnIndex("recipeName"));
        String servingSize = cursor.getString(cursor.getColumnIndex("servingSize"));
        String calories = cursor.getString(cursor.getColumnIndex("calories"));
        String sugarContent = cursor.getString(cursor.getColumnIndex("sugarContent"));
        String sodiumContent = cursor.getString(cursor.getColumnIndex("sodiumContent"));
        String fatContent = cursor.getString(cursor.getColumnIndex("fatContent"));
        String saturatedFat = cursor.getString(cursor.getColumnIndex("saturatedFatContent"));
        String transFat = cursor.getString(cursor.getColumnIndex("transFatContent"));
        String carbs = cursor.getString(cursor.getColumnIndex("carbohydrateContent"));
        String fiber = cursor.getString(cursor.getColumnIndex("fiberContent"));
        String protein = cursor.getString(cursor.getColumnIndex("proteinContent"));
        String cholesterol = cursor.getString(cursor.getColumnIndex("cholesterolContent"));
        cursor.close();
        db.close();
        return new Nutrition(Integer.parseInt(id), name, servingSize, calories, sugarContent, sodiumContent,
                fatContent, saturatedFat, transFat, carbs, fiber, protein, cholesterol);
    }

    public List<Nutrition> buildNutritionListFromDb() {
        return getAllNutritions();
    }

    private String runQuerySingle(String sql, String[] params, String column) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, params);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        String output = cursor.getString(cursor.getColumnIndex(column));
        cursor.close();
        db.close();
        return output;
    }

    //Should use buildNutritionListFromDb for all outside uses
    //I know that's bad grammar; LOL just me here though ^u^'
    public List<Nutrition> getAllNutritions() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Nutrition", new String[] {});
        cursor.moveToFirst();
        List<Nutrition> nutritions = new ArrayList<>();
        while ( !cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex("_ID"));
            String name = cursor.getString(cursor.getColumnIndex("recipeName"));
            String servingSize = cursor.getString(cursor.getColumnIndex("servingSize"));
            String calories = cursor.getString(cursor.getColumnIndex("calories"));
            String sugarContent = cursor.getString(cursor.getColumnIndex("sugarContent"));
            String sodiumContent = cursor.getString(cursor.getColumnIndex("sodiumContent"));
            String fatContent = cursor.getString(cursor.getColumnIndex("fatContent"));
            String saturatedFat = cursor.getString(cursor.getColumnIndex("saturatedFatContent"));
            String transFat = cursor.getString(cursor.getColumnIndex("transFatContent"));
            String carbs = cursor.getString(cursor.getColumnIndex("carbohydrateContent"));
            String fiber = cursor.getString(cursor.getColumnIndex("fiberContent"));
            String protein = cursor.getString(cursor.getColumnIndex("proteinContent"));
            String cholesterol = cursor.getString(cursor.getColumnIndex("cholesterolContent"));
            nutritions.add(new Nutrition(id, name, servingSize, calories, sugarContent, sodiumContent, fatContent, saturatedFat,
                    transFat, carbs, fiber, protein, cholesterol));
        }
        cursor.close();
        db.close();
        return nutritions;
    }

    //DOUBLE CHECK!!! Probably needs to be int...
    public String getNutritionName(String id) {
        String sql = "SELECT recipeName FROM Nutrition WHERE _ID = ?";
        return runQuerySingle(sql, new String[] {id}, "recipeName");
    }

    public String getNutritionId(String name) {
        String sql = "SELECT id FROM Nutrition WHERE recipeName = ?";
        return runQuerySingle(sql, new String[] {name}, "_ID");
    }
}

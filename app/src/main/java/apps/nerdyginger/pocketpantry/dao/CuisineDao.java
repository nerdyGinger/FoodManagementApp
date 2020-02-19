package apps.nerdyginger.pocketpantry.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.collection.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.helpers.DatabaseHelper;
import apps.nerdyginger.pocketpantry.models.Cuisine;

public class CuisineDao {
    private Context context;

    public CuisineDao(Context context) {
        this.context = context;
    }

    public Cuisine buildCategoryFromId(String id) {
        String name = getCuisineName(id);
        return new Cuisine(Integer.parseInt(id), name);
    }

    public Cuisine buildCategoryFromName(String name) {
        String id = getCuisineId(name);
        return new Cuisine(Integer.parseInt(id), name);
    }

    public List<Cuisine> buildCategoryListFromDb() {
        ArrayMap<String, String> valuePairs = getAllCuisines();
        List<Cuisine> cuisineList = new ArrayList<>();
        for (int i=0; i<valuePairs.size(); i++) {
            Cuisine pair = new Cuisine(Integer.parseInt(valuePairs.keyAt(i)), valuePairs.valueAt(i));
            cuisineList.add(pair);
        }
        return cuisineList;
    }

    public List<String> getAllCuisineNames() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Cuisine", new String[] {});
        List<String> cuisines = new ArrayList<>();
        try{
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                cuisines.add(name);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return cuisines;
    }

    public ArrayMap<String, String> getAllCuisines() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Cuisine", new String[] {});
        cursor.moveToFirst();
        ArrayMap<String, String> cuisines = new ArrayMap<>();
        while ( !cursor.isAfterLast()) {
            Integer id = cursor.getInt(cursor.getColumnIndex("_ID"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            cuisines.put(id.toString(), name);
        }
        cursor.close();
        db.close();
        return cuisines;
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

    public String getCuisineId(String name) {
        String sql = "Select _ID from Cuisine where name = ?";
        return runQuerySingle(sql, new String[] {name}, "_ID");
    }

    //DOUBLE CHECK!!! Probably needs to be int...
    public String getCuisineName(String id) {
        String sql = "Select name from Cuisine where _ID = ?";
        return runQuerySingle(sql, new String[] {id}, "name");
    }
}

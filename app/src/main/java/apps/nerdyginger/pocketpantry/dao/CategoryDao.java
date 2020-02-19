package apps.nerdyginger.pocketpantry.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.collection.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.models.Category;
import apps.nerdyginger.pocketpantry.helpers.DatabaseHelper;

public class CategoryDao {
    private Context context;

    public CategoryDao(Context context) {
        this.context = context;
    }

    public Category buildCategoryFromId(String id) {
        String name = getCategoryName(id);
        return new Category(Integer.parseInt(id), name);
    }

    public Category buildCategoryFromName(String name) {
        String id = getCategoryId(name);
        return new Category(Integer.parseInt(id), name);
    }

    public List<Category> buildCategoryListFromDb() {
        ArrayMap<String, String> valuePairs = getAllCategories();
        List<Category> categoryList = new ArrayList<>();
        for (int i=0; i<valuePairs.size(); i++) {
            Category pair = new Category(Integer.parseInt(valuePairs.keyAt(i)), valuePairs.valueAt(i));
            categoryList.add(pair);
        }
        return categoryList;
    }

    public List<String> getAllCategoryNames() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Category", new String[] {});
        List<String> categories = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                categories.add(name);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return categories;
    }

    public ArrayMap<String, String> getAllCategories() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Category", new String[] {});
        ArrayMap<String, String> categories = new ArrayMap<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                Integer id = cursor.getInt(cursor.getColumnIndex("_ID"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                categories.put(id.toString(), name);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return categories;
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

    public String getCategoryId(String name) {
        String sql = "Select _ID from Category where name = ?";
        return runQuerySingle(sql, new String[] {name}, "_ID");
    }

    //DOUBLE CHECK!!! Probably needs to be int...
    public String getCategoryName(String id) {
        String sql = "Select name from Category where _ID = ?";
        return runQuerySingle(sql, new String[] {id}, "name");
    }
}

package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.collection.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.models.Category;
import apps.nerdyginger.cleanplateclub.DatabaseHelper;

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

    public ArrayMap<String, String> getAllCategories() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Category", new String[] {});
        cursor.moveToFirst();
        ArrayMap<String, String> categories = new ArrayMap<>();
        while ( !cursor.isAfterLast()) {
            Integer id = cursor.getInt(cursor.getColumnIndex("_ID"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            categories.put(id.toString(), name);
        }
        cursor.close();
        return categories;
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

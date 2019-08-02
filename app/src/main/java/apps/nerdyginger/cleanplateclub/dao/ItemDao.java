package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.Converters;
import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.Item;

public class ItemDao {
    private Context context;

    public ItemDao(Context context) {
        this.context = context;
    }

    public Item getItemFromId(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Item where rowid = ?";
        String name = "";
        int flavorId = 0, categoryId = 0;
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            name = cursor.getString(cursor.getColumnIndex("name"));
            flavorId = cursor.getInt(cursor.getColumnIndex("flavor"));
            categoryId = cursor.getInt(cursor.getColumnIndex("category"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return new Item(Integer.parseInt(id), name, flavorId, categoryId);
    }

    public List<Item> buildItemListFromDb() {
        return getAllItems();
    }

    //Should use buildItemListFromDb for all outside uses
    public List<Item> getAllItems() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Item", new String[] {});
        List<Item> items = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("rowid"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int flavorId = cursor.getInt(cursor.getColumnIndex("flavor"));
                int categoryId = cursor.getInt(cursor.getColumnIndex("category"));
                items.add(new Item(id, name, flavorId, categoryId));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return items;
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

    public String getItemName(String id) {
        String sql = "Select name from Item where rowid = ?";
        return runQuerySingle(sql, new String[] {id}, "name");
    }

    public String getItemId(String name) {
        String sql = "Select rowid from Item where name = ?";
        return runQuerySingle(sql, new String[] {name}, "rowid");
    }
}

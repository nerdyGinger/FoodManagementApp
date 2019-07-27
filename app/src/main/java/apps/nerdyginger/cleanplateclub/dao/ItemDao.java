package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.Item;

public class ItemDao {
    private Context context;

    public ItemDao(Context context) {
        this.context = context;
    }

    public Item buildItemFromId(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Item where _ID = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
        }
        String name = cursor.getString(cursor.getColumnIndex("name"));
        int flavorId = cursor.getInt(cursor.getColumnIndex("flavor"));
        int categoryId = cursor.getInt(cursor.getColumnIndex("category"));
        cursor.close();
        db.close();
        return new Item(Integer.parseInt(id), name, flavorId, categoryId);
    }

    public List<Item> buildItemListFromDb() {
        return getAllItems();
    }

    //Should use buildItemListFromDb for all outside uses
    public List<Item> getAllItems() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Item", new String[] {});
        cursor.moveToFirst();
        List<Item> items = new ArrayList<>();
        while ( !cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex("_ID"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int flavorId = cursor.getInt(cursor.getColumnIndex("flavor"));
            int categoryId = cursor.getInt(cursor.getColumnIndex("category"));
            items.add(new Item(id, name, flavorId, categoryId));
        }
        cursor.close();
        db.close();
        return items;
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

    public String getItemName(String id) {
        String sql = "Select name from Item where _ID = ?";
        return runQuerySingle(sql, new String[] {id}, "name");
    }

    public String getItemId(String name) {
        String sql = "Select _ID from Item where name = ?";
        return runQuerySingle(sql, new String[] {name}, "_ID");
    }
}

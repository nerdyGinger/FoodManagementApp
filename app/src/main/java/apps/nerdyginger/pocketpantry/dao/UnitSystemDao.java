package apps.nerdyginger.pocketpantry.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.helpers.DatabaseHelper;
import apps.nerdyginger.pocketpantry.models.UnitSystem;

public class UnitSystemDao {
    private Context context;

    public UnitSystemDao(Context context) {
        this.context = context;
    }

    public UnitSystem getSystemById(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from UnitSystem where _ID = ?";
        String name = "";
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            name = cursor.getString(cursor.getColumnIndex("name"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return new UnitSystem(Integer.parseInt(id), name);
    }

    public String getSystemIdByName(String name) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select _ID from UnitSystem where name = ?";
        String id = "";
        Cursor cursor = db.rawQuery(sql, new String[] {name});
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            id = cursor.getString(cursor.getColumnIndex("_ID"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return id;
    }

    public List<UnitSystem> getAllSystems() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from UnitSystems";
        Cursor cursor = db.rawQuery(sql, new String[] {});
        List<UnitSystem> units = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("_ID"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                units.add(new UnitSystem(id, name));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return units;
    }
}

package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.UnitSystem;

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

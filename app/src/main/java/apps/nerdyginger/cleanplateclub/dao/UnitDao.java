package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.Unit;

public class UnitDao {
    private Context context;

    public UnitDao(Context context) {
        this.context = context;
    }

    public Unit getUnitById(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Unit where _ID = ?";
        String fullName = "", abbrev = "";
        int systemId = 0;
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            fullName = cursor.getString(cursor.getColumnIndex("fullName"));
            abbrev = cursor.getString(cursor.getColumnIndex("abbreviation"));
            systemId = cursor.getInt(cursor.getColumnIndex("systemId"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return new Unit(Integer.parseInt(id), fullName, abbrev, systemId);
    }

    public List<Unit> getAllUnits() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Unit";
        Cursor cursor = db.rawQuery(sql, new String[] {});
        List<Unit> units = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("_ID"));
                String fullName = cursor.getString(cursor.getColumnIndex("fullName"));
                String abbrev = cursor.getString(cursor.getColumnIndex("abbreviation"));
                int systemId = cursor.getInt(cursor.getColumnIndex("systemId"));
                units.add(new Unit(id, fullName, abbrev, systemId));
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
package apps.nerdyginger.pocketpantry.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.DatabaseHelper;
import apps.nerdyginger.pocketpantry.models.Unit;

public class UnitDao {
    private Context context;

    public UnitDao(Context context) {
        this.context = context;
    }

    public Unit getUnitById(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Unit where _ID = ?";
        String fullName = "", abbrev = "", type = "";
        int systemId = 0;
        Cursor cursor = db.rawQuery(sql, new String[] {id});
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            fullName = cursor.getString(cursor.getColumnIndex("fullName"));
            abbrev = cursor.getString(cursor.getColumnIndex("abbreviation"));
            systemId = cursor.getInt(cursor.getColumnIndex("systemId"));
            type = cursor.getString(cursor.getColumnIndex("type"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return new Unit(Integer.parseInt(id), fullName, abbrev, systemId, type);
    }

    public String getUnitAbbrevByName(String unitName) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select abbreviation from Unit where fullName = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {unitName});
        String abbrev = "";
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                abbrev = (cursor.getString(cursor.getColumnIndex("abbreviation")));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return abbrev;
    }

    public Unit getUnitByAbbrev(String abbrev) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Unit where abbreviation = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {abbrev});
        String name, type;
        int id, sysId;
        Unit unit = new Unit();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                id = (cursor.getInt(cursor.getColumnIndex("_ID")));
                name = (cursor.getString(cursor.getColumnIndex("fullName")));
                type = (cursor.getString(cursor.getColumnIndex("type")));
                sysId = (cursor.getInt(cursor.getColumnIndex("systemId")));
                unit = new Unit(id, name, abbrev, sysId, type);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return unit;
    }

    public Unit getUnitByName(String name) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Unit where fullName = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {name});
        String abbrev, type;
        int id, sysId;
        Unit unit = new Unit();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                id = (cursor.getInt(cursor.getColumnIndex("_ID")));
                abbrev = (cursor.getString(cursor.getColumnIndex("abbreviation")));
                type = (cursor.getString(cursor.getColumnIndex("type")));
                sysId = (cursor.getInt(cursor.getColumnIndex("systemId")));
                unit = new Unit(id, name, abbrev, sysId, type);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return unit;
    }

    public List<String> getAllUnitNames() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select fullName from Unit";
        Cursor cursor = db.rawQuery(sql, new String[] {});
        List<String> names = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while( !cursor.isAfterLast()) {
                names.add(cursor.getString(cursor.getColumnIndex("fullName")));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return names;
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
                String type = cursor.getString(cursor.getColumnIndex("type"));
                int systemId = cursor.getInt(cursor.getColumnIndex("systemId"));
                units.add(new Unit(id, fullName, abbrev, systemId, type));
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

    public List<String> getAllUnitNamesBySystemId(String systemId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Unit where systemId = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {systemId});
        List<String> units = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                String fullName = cursor.getString(cursor.getColumnIndex("fullName"));
                units.add(fullName);
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

    public List<String> getAllUnitAbbrevsBySystemId(String systemId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Unit where systemId = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {systemId});
        List<String> units = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                String abbrev = cursor.getString(cursor.getColumnIndex("abbreviation"));
                units.add(abbrev);
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

    public String getUnitIdByNameAndSystem(String unitName, String systemId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select _ID from Unit where fullName = ? and systemId = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {unitName, systemId});
        int unitId = 0;
        try {

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            unitId = cursor.getInt(cursor.getColumnIndex("_ID"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return String.valueOf(unitId);
    }
}

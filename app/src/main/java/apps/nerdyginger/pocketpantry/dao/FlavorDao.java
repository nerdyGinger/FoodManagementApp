package apps.nerdyginger.pocketpantry.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.collection.ArrayMap;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.DatabaseHelper;
import apps.nerdyginger.pocketpantry.models.Flavor;

public class FlavorDao {
    private Context context;

    public FlavorDao(Context context) {
        this.context = context;
    }

    public Flavor buildFlavorFromId(String id) {
        String name = getFlavorName(id);
        return new Flavor(Integer.parseInt(id), name);
    }

    public Flavor buildFlavorFromName(String name) {
        String id = getFlavorId(name);
        return new Flavor(Integer.parseInt(id), name);
    }

    public List<Flavor> buildFlavorListFromDb() {
        ArrayMap<String, String> valuePairs = getAllFlavors();
        List<Flavor> flavorList = new ArrayList<>();
        for (int i=0; i<valuePairs.size(); i++) {
            Flavor pair = new Flavor(Integer.parseInt(valuePairs.keyAt(i)), valuePairs.valueAt(i));
            flavorList.add(pair);
        }
        return flavorList;
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

    public ArrayMap<String, String> getAllFlavors() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Flavor", new String[] {});
        ArrayMap<String, String> flavors = new ArrayMap<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                Integer id = cursor.getInt(cursor.getColumnIndex("_ID"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                flavors.put(id.toString(), name);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return flavors;
    }

    //DOUBLE CHECK!!! Probably needs to be int...
    public String getFlavorName(String id) {
        String sql = "SELECT name FROM Flavor WHERE _ID = ?";
        return runQuerySingle(sql, new String[] {id}, "name");
    }

    public String getFlavorId(String name) {
        String sql = "SELECT id FROM Flavor WHERE name = ?";
        return runQuerySingle(sql, new String[] {name}, "_ID");
    }
}

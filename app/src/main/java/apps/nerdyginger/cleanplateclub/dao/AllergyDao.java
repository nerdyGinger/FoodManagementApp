package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.collection.ArrayMap;
import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.models.Allergy;
import apps.nerdyginger.cleanplateclub.DatabaseHelper;

public class AllergyDao {
    private Context context;

    public AllergyDao(Context context) {
        this.context = context;
    }

    public Allergy buildAllergyFromId(String id) {
        String name = getAllergyName(id);
        return new Allergy(Integer.parseInt(id), name);
    }

    public Allergy buildAllergyFromName(String name) {
        String id = getAllergyId(name);
        return new Allergy(Integer.parseInt(id), name);
    }

    public List<Allergy> buildAllergyListFromDb() {
        ArrayMap<String, String> valuePairs = getAllAllergies();
        List<Allergy> allergyList = new ArrayList<>();
        for (int i=0; i<valuePairs.size(); i++) {
            Allergy pair = new Allergy(Integer.parseInt(valuePairs.keyAt(i)), valuePairs.valueAt(i));
            allergyList.add(pair);
        }
        return allergyList;
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

    public ArrayMap<String, String> getAllAllergies() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Allergy", new String[] {});
        ArrayMap<String, String> allergies = new ArrayMap<>();
        try {
            cursor.moveToFirst();
            while ( !cursor.isAfterLast()) {
                Integer id = cursor.getInt(cursor.getColumnIndex("_ID"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                allergies.put(id.toString(), name);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return allergies;
    }

    //DOUBLE CHECK!!! Probably needs to be int...
    public String getAllergyName(String id) {
        String sql = "SELECT name FROM Allergy WHERE _ID = ?";
        return runQuerySingle(sql, new String[] {id}, "name");
    }

    public String getAllergyId(String name) {
        String sql = "SELECT id FROM Allergy WHERE name = ?";
        return runQuerySingle(sql, new String[] {name}, "_ID");
    }
}

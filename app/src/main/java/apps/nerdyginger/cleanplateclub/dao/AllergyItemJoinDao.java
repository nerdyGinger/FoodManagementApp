package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.Allergy;
import apps.nerdyginger.cleanplateclub.models.Item;

public class AllergyItemJoinDao {
    private Context context;

    public AllergyItemJoinDao(Context context) {
        this.context = context;
    }

    public List<Allergy> getAllergiesByItem(String itemId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "Select * from Allergy INNER JOIN AllergyItemJoin ON Allergy._ID=AllergyItemJoin.allergyId WHERE AllergyItemJoin.itemId = ?";
        String[] params = new String[] {itemId};
        Cursor cursor = db.rawQuery(sql, params);
        List<Allergy> allergies = new ArrayList<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                AllergyDao allergyDao = new AllergyDao(context);
                while (!cursor.isAfterLast()) {
                    Integer id = cursor.getInt(cursor.getColumnIndex("allergyId"));
                    allergies.add(allergyDao.buildAllergyFromId(id.toString()));
                }
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return allergies;
    }

    public List<Item> getItemsByAllergy(String allergyId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        String sql = "SELECT * FROM Item INNER JOIN AllergyItemJoin ON Item.rowid=AllergyItemJoin.itemId WHERE AllergyItemJoin.allergyId = ?";
        String[] params = new String[] {allergyId};
        Cursor cursor = db.rawQuery(sql, params);
        List<Item> items = new ArrayList<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ItemDao itemDao = new ItemDao(context);
                while (!cursor.isAfterLast()) {
                    Integer id = cursor.getInt(cursor.getColumnIndex("itemId"));
                    items.add(itemDao.getItemFromId(id.toString()));
                }
            }
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return items;
    }
}

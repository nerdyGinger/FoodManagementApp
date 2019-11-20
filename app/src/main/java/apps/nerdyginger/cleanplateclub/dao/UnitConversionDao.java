package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.models.Unit;

/*
    Giving credit where credit is due, because I got this idea from someone else--thanks!
    https://dba.stackexchange.com/questions/18539/best-way-to-store-units-in-database
 */
public class UnitConversionDao {
    private Context context;

    public UnitConversionDao(Context context) {
        this.context = context;
    }

    public int convertUnitQuantity(int quantity, String unitFromId, String unitToId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        UnitDao unitDao = new UnitDao(context);
        Unit fromUnit = unitDao.getUnitById(unitFromId);
        Unit toUnit = unitDao.getUnitById(unitToId);
        String sql = "Select * from UnitConversion where fromUnit=? and toUnit=?";
        int fromOffset = 0, multiplicand = 1, denominator = 1, toOffset = 0;
        Cursor cursor = db.rawQuery(sql, new String[] {unitFromId, unitToId});
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            fromOffset = cursor.getInt(cursor.getColumnIndex("fromOffset"));
            multiplicand = cursor.getInt(cursor.getColumnIndex("multiplicand"));
            denominator = cursor.getInt(cursor.getColumnIndex("denominator"));
            toOffset = cursor.getInt(cursor.getColumnIndex("toOffset"));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return ((quantity + fromOffset) * multiplicand / denominator) + toOffset;
    }
}

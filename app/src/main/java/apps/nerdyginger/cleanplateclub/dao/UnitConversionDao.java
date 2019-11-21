package apps.nerdyginger.cleanplateclub.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

import apps.nerdyginger.cleanplateclub.DatabaseHelper;
import apps.nerdyginger.cleanplateclub.Fraction;
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

    public Fraction convertUnitQuantity(Fraction quantity, String unitFromId, String unitToId) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        UnitDao unitDao = new UnitDao(context);
        Unit fromUnit = unitDao.getUnitById(unitFromId);
        Unit toUnit = unitDao.getUnitById(unitToId);
        String sql = "Select * from UnitConversion where fromUnit=? and toUnit=?";
        Fraction fromOffset = new Fraction(0, 0, 0);
        Fraction multiplicand = new Fraction(1, 0, 0);
        Fraction denominator = new Fraction(1, 0, 0);
        Fraction toOffset = new Fraction(0, 0, 0);
        Cursor cursor = db.rawQuery(sql, new String[] {unitFromId, unitToId});
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
            fromOffset.setWholeNum(cursor.getInt(cursor.getColumnIndex("fromOffset")));
            multiplicand.setWholeNum(cursor.getInt(cursor.getColumnIndex("multiplicand")));
            denominator.setWholeNum(cursor.getInt(cursor.getColumnIndex("denominator")));
            toOffset.setWholeNum(cursor.getInt(cursor.getColumnIndex("toOffset")));
        } catch (Exception e) {
            Log.e("Database Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }

        /*
        Fraction step1 = quantity.add(fromOffset);
        Log.e("CONVERSION_DEBUG", "STEP 1: " + quantity.toString() + " + " + fromOffset.toString() + " = " + step1.toString());
        Fraction step2 = step1.multiply(multiplicand);
        Log.e("CONVERSION_DEBUG", "STEP 2: " + step1.toString() + " * " + multiplicand.toString() + " = " + step2.toString());
        Fraction step3 = step2.divide(denominator);
        Log.e("CONVERSION_DEBUG", "STEP 3: " + step2.toString() + " / " + denominator.toString() + " = " + step3.toString());
        Fraction step4 = step3.add(toOffset);
        Log.e("CONVERSION_DEBUG", "STEP 4: " + step3.toString() + " + " + toOffset.toString() + " = " + step4.toString());


        Log.e( "CONVERSION_DEBUG" ,
                "( ( (" + quantity.toString() + " + " + fromOffset.toString() + ") * " + multiplicand.toString() + ") / " + denominator.toString() + " ) + " + toOffset.toString());
         */

        return ( ( (quantity.add(fromOffset) ).multiply(multiplicand) ).divide(denominator) ).add(toOffset);
    }
}

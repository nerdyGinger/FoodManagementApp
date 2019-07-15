package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String ASSETS_PATH = "databases";
    private static String DATABASE_NAME = "food.sqlite3";
    private static int DATABASE_VERSION = 1;
    private Context mContext;
    private SharedPreferences preferences;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        setPreferences();
    }

    private void setPreferences() {
        preferences = mContext.getSharedPreferences(mContext.getPackageName()+".database_versions", Context.MODE_PRIVATE);
    }

    private boolean installedDatabaseIsOutdated() {
        return preferences.getInt(DATABASE_NAME, 0) < DATABASE_VERSION;
    }

    private void writeDatabaseVersionInPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(DATABASE_NAME, DATABASE_VERSION);
        editor.apply();
    }

    private void installDatabaseFromAssets() {
        try {
            InputStream inStream = mContext.getAssets().open(ASSETS_PATH + "/" + DATABASE_NAME);
            File outputFile = new File(mContext.getDatabasePath(DATABASE_NAME).getPath());
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = inStream.read(buffer, 0, 8192)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inStream.close();

        } catch (Throwable e) {
            throw new RuntimeException("The " + DATABASE_NAME + " database could not be installed.", e);
        }
    }

    private void installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            mContext.deleteDatabase(DATABASE_NAME);
            installDatabaseFromAssets();
            writeDatabaseVersionInPreferences();
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        throw new RuntimeException("The " + DATABASE_NAME + " database is not writable.");
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        installOrUpdateIfNecessary();
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Nothing to do
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing to do
    }
}

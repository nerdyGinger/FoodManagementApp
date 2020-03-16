package apps.nerdyginger.pocketpantry.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.helpers.DatabaseHelper;
import apps.nerdyginger.pocketpantry.models.RecipeBook;

public class RecipeBookDao {
    private Context context;

    public RecipeBookDao(Context context) {
        this.context = context;
    }

    public List<RecipeBook> getAllRecipeBooks() {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM RecipeBook", new String[] {});
        List<RecipeBook> recipeBooks = new ArrayList<>();
        String name, author, description, imageUrl, url;
        int id;
        try {
            cursor.moveToFirst();
            while( ! cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("rowid"));
                name = cursor.getString(cursor.getColumnIndex("name"));
                author = cursor.getString(cursor.getColumnIndex("author"));
                description = cursor.getString(cursor.getColumnIndex("description"));
                imageUrl = cursor.getString(cursor.getColumnIndex("image"));
                url = cursor.getString(cursor.getColumnIndex("link"));
                recipeBooks.add(new RecipeBook(id, name, author, url, description, imageUrl));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("RecipeBook DB Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return recipeBooks;
    }

    public RecipeBook getRecipeBookById(String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM RecipeBook WHERE rowid = ?", new String[] {id});
        RecipeBook book = new RecipeBook();
        try {
            cursor.moveToFirst();
            book.set_ID(Integer.parseInt(id));
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            book.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            book.setImageUrl(cursor.getString(cursor.getColumnIndex("image")));
            book.setLink(cursor.getString(cursor.getColumnIndex("link")));
        }catch (Exception e) {
            Log.e("RecipeBook DB Error", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return book;
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
}

package apps.nerdyginger.pocketpantry.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.R;
import apps.nerdyginger.pocketpantry.dao.RecipeBookDao;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.RecipeBook;
import apps.nerdyginger.pocketpantry.models.UserRecipe;

// Helper class for handling the serialization and deserialization of images to
// and from internal storage using filename schema <recipeBookName>-<recipeName>.jpg
// PLAN: Download & serialize images on first run, deserialize from internal storage
// as needed.
// Last edited: 3/17/2020
public class ImageHelper {
    private String PATH = "recipeImages";
    private Context context;

    public ImageHelper(Context context) {
        this.context = context;
    }

    // downloads and stores all recipe & recipe book images for read-only db
    // ONLY to be run once on first run
    public void loadAllDbImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RecipeDao recipeDao = new RecipeDao(context);
                RecipeBookDao bookDao = new RecipeBookDao(context);
                List<Recipe> recipes = recipeDao.getAllRecipes();
                List<String> loadedBooks = new ArrayList<>();
                    loadedBooks.add("1");
                for (int i=0; i<recipes.size(); i++) {
                    Recipe recipe = recipes.get(i);
                    RecipeBook book = bookDao.getRecipeBookById(recipe.getRecipeBookId());
                    Log.i("ImageHelper", "Loading image for " + recipe.getName());
                    if ( ! loadedBooks.contains(recipe.getRecipeBookId())) {
                        Log.i("ImageHelper", "---Adding book cover for " + book.getName());
                        Bitmap bookCover = downloadImage(book.getImageUrl());
                        storeImage(bookCover, getFilename(book));
                        loadedBooks.add(recipe.getRecipeBookId());
                    }
                    if ( ! recipe.getImageUrl().isEmpty()) {
                        Log.i("ImageHelper", "---Adding image for " + recipe.getName());
                        Bitmap image = downloadImage(recipe.getImageUrl());
                        storeImage(image, getFilename(book, recipe));
                    }
                }
            }
        }).start();
    }

    // returns filename for recipe book cover image
    public String getFilename(RecipeBook book) {
        return book.getName() + "-" + "_COVER_";
    }

    // returns filename for recipe image from given book
    public String getFilename(RecipeBook book, Recipe recipe) {
        return book.getName() + "-" + recipe.getName();
    }

    // returns filename for recipe image from user recipes
    public String getFilename(UserRecipe recipe) {
        return "USER_ADDED" + "-" + recipe.getName() + "-" + recipe.get_ID();
    }

    // downloads image from url and returns as a bitmap object
    // adapted from: https://stackoverflow.com/questions/8992964/android-load-from-url-to-bitmap
    private Bitmap downloadImage(final String urlString) {
        final Bitmap[] bitmap = new Bitmap[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    bitmap[0] =  myBitmap;
                } catch (IOException e) {
                    Log.e("IMAGE_DOWNLOAD_ERROR", e.toString());
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for image download thread: " + e.toString());
        }
        return bitmap[0];
    }

    // retrieves bitmap image from internal storage
    // adapted from: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    public Bitmap retrieveImage(String filename) {
        Bitmap bitmap;
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir(PATH, Context.MODE_PRIVATE);
            File file = new File(directory, filename + ".jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch(Exception e) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_chef);
            Log.e("IMAGE_ERROR", e.toString());
        }
        return bitmap;
    }

    // stores bitmap image to internal storage directory
    // adapted from: https://www.tutorialspoint.com/how-to-write-an-image-file-in-internal-storage-in-android
    private void storeImage(Bitmap bitmap, String filename) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(PATH, Context.MODE_PRIVATE);
        File file = new File(directory, filename + ".jpg");
        if (!file.exists()) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                Log.e("IMAGE_STORE_ ERROR", e.toString());
            }
        }
    }
}

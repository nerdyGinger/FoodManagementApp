package apps.nerdyginger.pocketpantry.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

// Helper class for handling the serialization and deserialization of images to
// and from internal storage
// PLAN: Download & serialize images on first run, deserialize from internal storage
// as needed.
// Last edited: 2/24/2020
public class ImageHelper {
    private String PATH = "recipeImages";
    private Context context;

    public ImageHelper(Context context) {
        this.context = context;
    }

    // downloads image from url and returns as a bitmap object
    //public Bitmap downloadImage(String url) {

    //}

    // retrieves bitmap image from internal storage
    // adapted from: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    public Bitmap retrieveImage(String filename) {
        Bitmap bitmap = null;
        try {
            File file = new File(PATH, filename + ".jpg");
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch(Exception e) {
            Log.e("IMAGE_ERROR", e.toString());
        }
        return bitmap;
    }

    // stores bitmap image to internal storage directory
    // adapted from: https://www.tutorialspoint.com/how-to-write-an-image-file-in-internal-storage-in-android
    public void storeImage(Bitmap bitmap, String filename) {
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
                Log.e("IMAGE_ERROR", e.toString());
            }
        }
    }
}

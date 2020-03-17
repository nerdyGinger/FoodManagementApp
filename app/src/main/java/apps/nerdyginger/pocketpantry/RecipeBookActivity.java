package apps.nerdyginger.pocketpantry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import apps.nerdyginger.pocketpantry.dao.RecipeBookDao;
import apps.nerdyginger.pocketpantry.helpers.ImageHelper;
import apps.nerdyginger.pocketpantry.models.RecipeBook;

public class RecipeBookActivity extends AppCompatActivity {
    private RecipeBook recipeBook;
    private Context context;
    private ImageHelper imageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_book);
        context = getApplicationContext();
        imageHelper = new ImageHelper(context);

        // Get recipe book id
        Intent inIntent = getIntent();
        int recipeBookId = inIntent.getIntExtra("RecipeBookId", -1);
        if (recipeBookId == -1) {
            Log.e("RecipeBook ERROR", "RecipeBook ID not found in RecipeBookActivity");
        }
        getRecipeBook(String.valueOf(recipeBookId));

        // Find views
        TextView authorBox = findViewById(R.id.recipeBookInfoAuthor);
        TextView descriptionBox = findViewById(R.id.recipeBookInfoDescription);
        ImageView image = findViewById(R.id.recipeBookInfoImage);
        TextView linkBox = findViewById(R.id.recipeBookInfoLink);
        TextView nameBox = findViewById(R.id.recipeBookInfoName);
        RecyclerView rv = findViewById(R.id.recipeBookRecycler);

        // Fill views with content
        authorBox.setText(recipeBook.getAuthor());
        descriptionBox.setText(recipeBook.getDescription());

        //imageHelper.loadAllDbImages(); //TODO: make this a checkable status on startup (load on first run)
        image.setImageBitmap(imageHelper.retrieveImage(imageHelper.getFilename(recipeBook)));
        linkBox.setText(recipeBook.getLink());
        nameBox.setText(recipeBook.getName());

        // Fill up recycler with recipes from book

    }

    private void getRecipeBook(final String id) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                RecipeBookDao dao = new RecipeBookDao(context);
                recipeBook = dao.getRecipeBookById(id);
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
        }
    }
}

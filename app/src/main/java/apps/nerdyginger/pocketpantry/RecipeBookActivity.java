package apps.nerdyginger.pocketpantry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.pocketpantry.adapters.RecipesListAdapter;
import apps.nerdyginger.pocketpantry.dao.CategoryDao;
import apps.nerdyginger.pocketpantry.dao.RecipeBookDao;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.helpers.ImageHelper;
import apps.nerdyginger.pocketpantry.helpers.RecipeDialogHelper;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.RecipeBook;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;

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

        image.setImageBitmap(imageHelper.retrieveImage(imageHelper.getFilename(recipeBook)));
        linkBox.setText(recipeBook.getLink());
        nameBox.setText(recipeBook.getName());

        // Fill up recycler with recipes from book
        rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);
        RecipesListAdapter adapter = new RecipesListAdapter();
        List<Recipe> recipes = getRecipes(String.valueOf(recipeBookId));
        adapter.updateData(convertToBoxItems(recipes));
        rv.setAdapter(adapter);
    }

    private List<UserRecipeBoxItem> convertToBoxItems(List<Recipe> recipes) {
        List<UserRecipeBoxItem> boxItems = new ArrayList<>();
        for (Recipe recipe : recipes) {
            UserRecipeBoxItem item = new UserRecipeBoxItem();
            item.setRecipeId(recipe.get_ID());
            item.setUserAdded(false);
            item.setServings(recipe.getRecipeYield());
            item.setRecipeName(recipe.getName());
            item.setCategory(getCategory(recipe.getRecipeCategory()));
            boxItems.add(item);
        }
        return boxItems;
    }

    private List<Recipe> getRecipes(final String id) {
        final List<Recipe> recipes = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                RecipeDao dao = new RecipeDao(context);
                recipes.addAll(dao.getAllRecipesByBook(id));
            }
        });
        t.start();
        try {
            t.join();
            return recipes;
        } catch (Exception e) {
            Log.e("Thread Exception", "Problem waiting for db thread: " + e.toString());
            return recipes;
        }
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

    public String getCategory(String categoryId) {
        CategoryDao dao = new CategoryDao(context);
        return dao.getCategoryName(categoryId);
    }
}

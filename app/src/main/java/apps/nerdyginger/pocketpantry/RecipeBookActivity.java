package apps.nerdyginger.pocketpantry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RecipeBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_book);
        Intent inIntent = getIntent();
        int recipeBookId = inIntent.getIntExtra("RecipeBookId", -1);
        if (recipeBookId == -1) {
            Log.e("RecipeBook ERROR", "RecipeBook ID not found in RecipeBookActivity");
        }
        Toast.makeText(getApplicationContext(), "RecipeBookId: " + String.valueOf(recipeBookId),
                Toast.LENGTH_SHORT).show();
    }
}

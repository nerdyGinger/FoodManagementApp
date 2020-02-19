package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import apps.nerdyginger.pocketpantry.adapters.BrowseRecipesCategoryAdapter;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeDao;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserRecipe;


public class BrowseRecipeFragment extends Fragment {
    private Context context;
    private BrowseRecipesCategoryAdapter parentAdapter;

    public BrowseRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_recipe, container, false);

        RecyclerView categoriesRv = view.findViewById(R.id.browseRecipesRecycler);
        categoriesRv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        categoriesRv.setLayoutManager(llm);
        parentAdapter = new BrowseRecipesCategoryAdapter();
        categoriesRv.setAdapter(parentAdapter);



        return view;
    }

    private boolean inInventory(List<UserInventoryItem> inventoryItems, String itemName) {
        for (int i=0; i<inventoryItems.size(); i++) {
            if (inventoryItems.get(i).getItemName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    private void getBrowsableData() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // getting _all_ recipes
                UserCustomDatabase userDb = UserCustomDatabase.getDatabase(context);
                RecipeDao readOnlyDao = new RecipeDao(context);
                UserRecipeDao userDao = userDb.getUserRecipeDao();
                List<Recipe> readOnlyRecipes =  readOnlyDao.getAllRecipes();
                List<UserRecipe> userRecipes = userDao.getAllUserRecipes();

                // get inventory data
                UserInventoryItemDao inventoryDao = userDb.getUserInventoryDao();
                List<UserInventoryItem> inventoryItems = inventoryDao.getAllInventoryItems();

                // sort into categories, create List<UserRecipeBoxItem>s for each category

                // sort each list by relevance, i.e. how many ingredients user has on hand

                // create recommended list with recipes that user has all/most ingredients on hand (top 20?)

                // create quick trip list with recipes that are only missing 1-3 items (?)
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
            Log.e("Database Error", "Problem waiting for db thread to complete");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

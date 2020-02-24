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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.nerdyginger.pocketpantry.adapters.BrowseRecipesCategoryAdapter;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeDao;
import apps.nerdyginger.pocketpantry.helpers.SortRecipesHelper;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeCategory;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserRecipe;

// Fragment for the browse recipes page; displays recipes that
// are in the read-only db by category
// The heavy lifting for recipe sorting is handled by the SortRecipesHelper
// Last edited: 2/20/2020
public class BrowseRecipeFragment extends Fragment {
    private Context context;
    private BrowseRecipesCategoryAdapter parentAdapter;
    private SortRecipesHelper recipesHelper;

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

        // initialize global variables
        context = getContext();
        recipesHelper = new SortRecipesHelper(context);
        parentAdapter = new BrowseRecipesCategoryAdapter();

        // set up parent RecyclerView
        RecyclerView categoriesRv = view.findViewById(R.id.browseRecipesRecycler);
        categoriesRv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        categoriesRv.setLayoutManager(llm);
        categoriesRv.setAdapter(parentAdapter);

        // add data to adapter
        List<BrowseRecipeCategory> masterList = recipesHelper.getRecipesByCategory();
        //TODO: update categories to just be recipe categories
        Log.e("DEBUG", "HIT");
        masterList.addAll(recipesHelper.getRecipesByCuisine());
        Collections.shuffle(masterList); //shuffle categories so they are in a different order every time
        BrowseRecipeCategory recommended = new BrowseRecipeCategory();
        recommended.setCategoryName("Recommended For You");
        //recommended.setRecipeCards(recipesHelper.getRecommendedRecipes(masterList));
        //masterList.add(0, recommended); //TODO: Add back in once recipe data is in; it'll go forever with no data
                                            // (must have at least 20 recipes)

        parentAdapter.updateData(masterList);

        return view;
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

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.adapters.BrowseRecipesCategoryAdapter;
import apps.nerdyginger.pocketpantry.dao.RecipeDao;
import apps.nerdyginger.pocketpantry.dao.UserInventoryItemDao;
import apps.nerdyginger.pocketpantry.dao.UserRecipeDao;
import apps.nerdyginger.pocketpantry.dialogs.CustomRecipeDialog;
import apps.nerdyginger.pocketpantry.helpers.SortRecipesHelper;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeCategory;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeItem;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserRecipe;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;

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
        List<BrowseRecipeCategory> masterList = recipesHelper.getRecipesByCategory(); //TODO-VER1.0: (?) instead grab random keywords?
        masterList.addAll(recipesHelper.getRecipesByCuisine());
        Collections.shuffle(masterList); //shuffle categories so they are in a different order every time
        BrowseRecipeCategory recommended = new BrowseRecipeCategory();
        recommended.setCategoryName("Recommended For You");
        recommended.setRecipeCards(recipesHelper.getRecommendedRecipes(masterList));
        masterList.add(0, recommended);

        // set up recycler clicks
        BrowseRecipeClickListener listener = new BrowseRecipeClickListener() {
            @Override
            public void onClick(BrowseRecipeItem clickedItem) {
                //show recipe dialog in view mode for recipe
                CustomRecipeDialog dialog = new CustomRecipeDialog("view", convertToRecipeBoxItem(clickedItem), context);
                dialog.show(Objects.requireNonNull(getFragmentManager()), "view recipe");
            }

            @Override
            public boolean onLongClick(BrowseRecipeItem longClickedItem) {
                Toast.makeText(context, longClickedItem.getRecipeName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        parentAdapter.setChildListener(listener);

        parentAdapter.updateData(masterList);

        return view;
    }

    private UserRecipeBoxItem convertToRecipeBoxItem(BrowseRecipeItem browseItem) {
        UserRecipeBoxItem boxItem = new UserRecipeBoxItem();
        boxItem.setCategory(browseItem.getCategory());
        boxItem.setRecipeId(browseItem.getRecipeId());
        boxItem.setRecipeName(browseItem.getRecipeName());
        boxItem.setServings(browseItem.getServings());
        boxItem.setUserAdded(false); //TODO-VER1.0: add category for browsing user-added items?
        return boxItem;
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

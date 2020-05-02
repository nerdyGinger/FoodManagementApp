package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.SearchView;
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
import apps.nerdyginger.pocketpantry.helpers.ImageHelper;
import apps.nerdyginger.pocketpantry.helpers.SortRecipesHelper;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeCategory;
import apps.nerdyginger.pocketpantry.models.BrowseRecipeItem;
import apps.nerdyginger.pocketpantry.models.Recipe;
import apps.nerdyginger.pocketpantry.models.UserInventoryItem;
import apps.nerdyginger.pocketpantry.models.UserRecipe;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;
import pl.droidsonroids.gif.GifImageView;

// Fragment for the browse recipes page; displays recipes that
// are in the read-only db by category
// The heavy lifting for recipe sorting is handled by the SortRecipesHelper
// Last edited: 3/18/2020
public class BrowseRecipeFragment extends Fragment implements SearchView.OnQueryTextListener {
    private Context context;
    private BrowseRecipesCategoryAdapter parentAdapter;
    private SortRecipesHelper recipesHelper;
    private ImageHelper imageHelper;
    private List<BrowseRecipeCategory> masterList = new ArrayList<>();
    private GifImageView loader;

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
        imageHelper = new ImageHelper(context);
        parentAdapter = new BrowseRecipesCategoryAdapter();
        loader = view.findViewById(R.id.browseRecipesLoader);

        // set up search
        SearchView search = view.findViewById(R.id.browseRecipesSearchBar);
        search.setOnQueryTextListener(this);

        // set up parent RecyclerView
        RecyclerView categoriesRv = view.findViewById(R.id.browseRecipesRecycler);
        categoriesRv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        categoriesRv.setLayoutManager(llm);
        categoriesRv.setAdapter(parentAdapter);

        new LoadRecipeAsync(context).execute();

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

        return view;
    }

    public void updateRecycler(List<BrowseRecipeCategory> updatedData) {
        loader.setVisibility(View.GONE);
        masterList = updatedData;
        parentAdapter.updateData(masterList);
    }

    // This works, but I must admit that it is really hack-ish
    // TODO-VER1.0: revisit and revise to be more smart and make the compiler happier:)
    public class LoadRecipeAsync extends AsyncTask<Void, List<BrowseRecipeCategory>, Void> {
        private Context context;

        LoadRecipeAsync(Context context) {
            this.context = context;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(List<BrowseRecipeCategory>... values) {
            updateRecycler(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected final Void doInBackground(Void... params) {
            SortRecipesHelper recipesHelper = new SortRecipesHelper(context);
            List<BrowseRecipeCategory> list = new ArrayList<>();
            list = recipesHelper.getRecipesByCategory(); //TODO-VER1.0: (?) instead grab random keywords?
            publishProgress(list);
            list.addAll(recipesHelper.getRecipesByCuisine());
            Collections.shuffle(list); //shuffle categories so they are in a different order every time
            publishProgress(list);
            /*
            BrowseRecipeCategory recommended = new BrowseRecipeCategory();
            recommended.setCategoryName("Recommended For You");
            recommended.setRecipeCards(recipesHelper.getRecommendedRecipes(list));
            list.add(0, recommended);
            publishProgress(list);
             */
            return null;
        }
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

    @Override
    public boolean onQueryTextChange(String query) {
        parentAdapter.updateData(filterCategories(masterList, query));
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<BrowseRecipeCategory> filterCategories(List<BrowseRecipeCategory> categories, String query) {
        List<BrowseRecipeCategory> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (BrowseRecipeCategory category : categories) {
            List<BrowseRecipeItem> innerFiltered = filterItems(category.getRecipeCards(), lowerQuery);
            if (innerFiltered.size() > 0) {
                BrowseRecipeCategory temp = new BrowseRecipeCategory();
                temp.setCategoryName(category.getCategoryName());
                temp.setRecipeCards(innerFiltered);
                filtered.add(temp);
            }
        }
        return filtered;
    }

    //filter recipes by query text (expected in lower case)
    private List<BrowseRecipeItem> filterItems(List<BrowseRecipeItem> items, String query) {
        List<BrowseRecipeItem> filtered = new ArrayList<>();
        for (BrowseRecipeItem item : items) {
            if (item.getRecipeName().toLowerCase().contains(query)) {
                filtered.add(item);
            }
        }
        return filtered;
    }
}

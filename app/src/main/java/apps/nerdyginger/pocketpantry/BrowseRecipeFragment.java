package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.nerdyginger.pocketpantry.adapters.BrowseRecipesCategoryAdapter;


public class BrowseRecipeFragment extends Fragment {
    private Context context;

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
        categoriesRv.setAdapter(new BrowseRecipesCategoryAdapter());//generateRecipeCategories()));


        return view;
    }

    /*
    //TODO: Remove method when adding implementation is... added
    private List<BrowseRecipeCategory> generateRecipeCategories() {
        List<BrowseRecipeItem> collegeFare = new ArrayList<>();
        collegeFare.add(new BrowseRecipeItem("PB & J", "Quick 'n' Easy"));
        collegeFare.add(new BrowseRecipeItem("Microwave Pasta", "Quick 'n' Easy"));
        collegeFare.add(new BrowseRecipeItem("Easy Frozen Bento Lunch", "Lunch"));
        collegeFare.add(new BrowseRecipeItem("Instant Ramen", "Quick 'n' Easy"));

        List<BrowseRecipeItem> snacks = new ArrayList<>();
        snacks.add(new BrowseRecipeItem("Apple Granola Sandwich", "Healthy Snacks"));
        snacks.add(new BrowseRecipeItem("Puppy Chow", "Unhealthy Snacks"));
        snacks.add(new BrowseRecipeItem("Roasted Pumpkin Seeds", "Fall Favorites"));
        snacks.add(new BrowseRecipeItem("Grandma's Ooey Gooey Brownie Bites", "Desserts"));
        snacks.add(new BrowseRecipeItem("Carrot-Apple-slaw", "Sides"));
        snacks.add(new BrowseRecipeItem("Russian Tea Cookies", "Desserts"));


        List<BrowseRecipeCategory> parents = new ArrayList<>();
        parents.add(new BrowseRecipeCategory("College Fare", collegeFare));
        parents.add(new BrowseRecipeCategory("Snacks", snacks));
        return parents;
    }
    */

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

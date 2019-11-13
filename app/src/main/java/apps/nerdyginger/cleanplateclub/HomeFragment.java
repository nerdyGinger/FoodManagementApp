package apps.nerdyginger.cleanplateclub;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

import apps.nerdyginger.cleanplateclub.adapters.BrowseRecipesItemAdapter;
import apps.nerdyginger.cleanplateclub.models.BrowseRecipeItem;


public class HomeFragment extends Fragment {

    public HomeFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        // Scheduled recipes horizontal RecyclerView
        RecyclerView recipeSelection = view.findViewById(R.id.homeScheduledRecipes);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recipeSelection.setLayoutManager(llm);
        BrowseRecipesItemAdapter adapter = new BrowseRecipesItemAdapter();
        recipeSelection.setAdapter(adapter);
        adapter.updateData(generateRecipes());

        // Set up tabs
        TabHost tabHost = view.findViewById(R.id.homeTabHost);
        tabHost.setup();
        // Today's recipes tab
        TabHost.TabSpec spec = tabHost.newTabSpec("today");
        spec.setContent(R.id.homeTodayTab);
        spec.setIndicator("Today");
        tabHost.addTab(spec);
        // This week's recipes tab
        spec = tabHost.newTabSpec("this week");
        spec.setContent(R.id.homeThistWeekTab);
        spec.setIndicator("This Week");
        tabHost.addTab(spec);

        return view;
    }

    private List<BrowseRecipeItem> generateRecipes() {
        List<BrowseRecipeItem> snacks = new ArrayList<>();
        snacks.add(new BrowseRecipeItem("Apple Granola Sandwich", "Healthy Snacks"));
        snacks.add(new BrowseRecipeItem("Puppy Chow", "Unhealthy Snacks"));
        snacks.add(new BrowseRecipeItem("Roasted Pumpkin Seeds", "Fall Favorites"));
        snacks.add(new BrowseRecipeItem("Grandma's Ooey Gooey Brownie Bites", "Desserts"));
        snacks.add(new BrowseRecipeItem("Carrot-Apple-slaw", "Sides"));
        snacks.add(new BrowseRecipeItem("Russian Tea Cookies", "Desserts"));
        return snacks;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

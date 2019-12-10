package apps.nerdyginger.pocketpantry;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import apps.nerdyginger.pocketpantry.adapters.RecipesListAdapter;
import apps.nerdyginger.pocketpantry.models.UserRecipeBoxItem;
import apps.nerdyginger.pocketpantry.view_models.RecipeViewModel;


public class RecipesFragment extends Fragment {
    private UserCustomDatabase userDatabase;
    private Context context;
    private TextView customLabel, browseLabel;
    private FloatingActionButton addBtn, customBtn, browseBtn;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    private boolean fabMenuIsOpen = false;
    private RecipesListAdapter adapter;

    public RecipesFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_recipes, container, false);

        // Set up floating action button
        setupFabMenu(view);

        // Initialize user database
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context, UserCustomDatabase.class, "userDatabase")
                    .fallbackToDestructiveMigration() //don't do this in production!!! //TODO: write user db migrations to remove destructive migrations
                    .build();
        }

        // Set up RecyclerView
        RecyclerView rv = view.findViewById(R.id.recipesRecycler);
        rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomRecipeDialog dialog = new CustomRecipeDialog("view", adapter.getItemAtPosition(position));
                dialog.show(Objects.requireNonNull(getFragmentManager()), "input a recipe!");
            }

            @Override
            public boolean onLongClick(View view, int position) {
                //add onLongClickImplementation
                return true;
            }
        };
        adapter = new RecipesListAdapter(listener);
        //adapter.updateData(dataList[0]);
        rv.setAdapter(adapter);

        // Get recipes data
        RecipeViewModel recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        recipeViewModel.getRecipeList().observe(getViewLifecycleOwner(), new Observer<List<UserRecipeBoxItem>>() {
            @Override
            public void onChanged(List<UserRecipeBoxItem> userRecipeBoxItems) {
                adapter.updateData(userRecipeBoxItems);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecipeSwipeDeleteCallback(adapter, context, recipeViewModel));
        itemTouchHelper.attachToRecyclerView(rv);

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

    private void setupFabMenu(View parentView) {
        addBtn = parentView.findViewById(R.id.recipesFloatingAddBtn);
        customBtn = parentView.findViewById(R.id.fabMenuCustomBtn);
        customBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomRecipeDialog dialog = new CustomRecipeDialog();
                dialog.show(Objects.requireNonNull(getFragmentManager()), "input a recipe!");
                closeFab();
            }
        });
        customLabel = parentView.findViewById(R.id.fabMenuCustomLabel);
        browseBtn = parentView.findViewById(R.id.fabMenuBrowseBtn);
        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                transaction.replace(R.id.frame_container, new BrowseRecipeFragment());
                transaction.addToBackStack(null);
                closeFab();
                transaction.commit();
            }
        });
        browseLabel = parentView.findViewById(R.id.fabMenuBrowseLabel);
        fab_open = AnimationUtils.loadAnimation(context, R.anim.fab_menu_open);
        fab_close = AnimationUtils.loadAnimation(context, R.anim.fab_menu_close);
        fab_clock = AnimationUtils.loadAnimation(context, R.anim.fab_icon_clockwise);
        fab_anticlock = AnimationUtils.loadAnimation(context, R.anim.fab_icon_anticlockwise);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenuIsOpen) {
                    closeFab();
                } else {
                    openFab();
                }

            }
        });
    }

    private void openFab() {
        customLabel.setVisibility(View.VISIBLE);
        browseLabel.setVisibility(View.VISIBLE);
        customBtn.startAnimation(fab_open);
        browseBtn.startAnimation(fab_open);
        addBtn.startAnimation(fab_clock);
        customBtn.setClickable(true);
        browseBtn.setClickable(true);
        fabMenuIsOpen = true;
    }

    private void closeFab() {
        customLabel.setVisibility(View.INVISIBLE);
        browseLabel.setVisibility(View.INVISIBLE);
        customBtn.startAnimation(fab_close);
        browseBtn.startAnimation(fab_close);
        addBtn.startAnimation(fab_anticlock);
        customBtn.setClickable(false);
        browseBtn.setClickable(false);
        fabMenuIsOpen = false;
    }
}
